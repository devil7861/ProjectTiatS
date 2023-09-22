package com.jica.newpts.MainFragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.jica.newpts.R;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Response;

public class SearchFragment extends Fragment {

    private RecyclerView weatherRecyclerView;
    private TextView tvDate;
    private String base_date; // 발표 일자
    private String base_time;// 발표 시각
    private Point curPoint;  // 현재 위치의 격자 좌표를 저장할 포인트
    TextView tvFSAddress;


    // GPS 활성화 요청 코드
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    // 런타임 퍼미션 요청 코드
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    // 필요한 위치 권한 목록
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    TextView textview_address;
    int weatherNumber = 6; // 리사이클러뷰에서 보여줄 날씨예보 개수
    private Geocoder geocoder;

    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // XML에서 UI 요소들을 참조
        tvDate = view.findViewById(R.id.tvDate);    // 오늘 날짜 텍스트뷰
        weatherRecyclerView = view.findViewById(R.id.weatherRecyclerView);  // 날씨 리사이클러 뷰
        Button btnRefresh = view.findViewById(R.id.btnRefresh);     // 새로고침 버튼
        textview_address = view.findViewById(R.id.textview);
        tvFSAddress = view.findViewById(R.id.tvFSAddress);

        // 리사이클러뷰 레이아웃 매니저 설정
        weatherRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 위치 서비스 상태 확인
        if (!checkLocationServicesStatus()) {
            // 위치 서비스가 비활성화되어 있으면 설정 다이얼로그 표시
            showDialogForLocationServiceSetting();
        } else {
            // 위치 서비스가 활성화되어 있으면 런타임 퍼미션 확인
            checkRunTimePermission();
        }

        // 새로고침 버튼 클릭 시 위치와 날씨 정보 다시 요청
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestLocationAndWeather();
            }
        });
    }

    // 날씨 정보와 위치 정보 설정
    private void setWeatherAndLocation(int nx, int ny) {
        // 준비 단계 : base_date(발표 일자), base_time(발표 시각)
        // 현재 날짜, 시간 정보 가져오기
        Calendar cal = Calendar.getInstance();
        base_date = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(cal.getTime()); // 현재 날짜
        String timeH = new SimpleDateFormat("HH", Locale.getDefault()).format(cal.getTime());// 현재 시각
        String timeM = new SimpleDateFormat("mm", Locale.getDefault()).format(cal.getTime()); // 현재 분
        base_time = new Common().getBaseTime(timeH, timeM);

        // 현재 시각이 00시이고 45분 이하여서 baseTime이 2330이면 어제 정보 받아오기
        if ("00".equals(timeH) && "2330".equals(base_time)) {
            cal.add(Calendar.DATE, -1);
            base_date = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(cal.getTime());
        }

        // API를 통해 날씨 정보 요청
        // (한 페이지 결과 수 = 60, 페이지 번호 = 1, 응답 자료 형식-"JSON", 발표 날싸, 발표 시각, 예보지점 좌표)
        Call<WEATHER> call = ApiObject.getRetrofitService().GetWeather(60, 1, "JSON", base_date, base_time, String.valueOf(nx), String.valueOf(ny));

        // 비동기적으로 실행하기
        call.enqueue(new retrofit2.Callback<WEATHER>() {
            // 응답 성공 시
            @Override
            public void onResponse(Call<WEATHER> call, Response<WEATHER> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // 날씨 정보 가져오기
                    List<ITEM> it = response.body().response.body.items.item;

                    // 현재 시각부터 1시간 뒤의 날씨 6개를 담을 배열
                    ModelWeather[] weatherArr = new ModelWeather[weatherNumber];
                    for (int i = 0; i < weatherArr.length; i++) {
                        weatherArr[i] = new ModelWeather();
                    }
                    // 배열 채우기
                    int index = 0;
                    int totalCount = response.body().response.body.totalCount - 1;
                    for (int i = 0; i <= totalCount; i++) {
                        index %= weatherNumber;
                        switch (it.get(i).category) {
                            case "PTY":
                                weatherArr[index].setRainType(it.get(i).fcstValue);
                                break;
                            case "REH":
                                weatherArr[index].setHumidity(it.get(i).fcstValue);
                                break;
                            case "SKY":
                                weatherArr[index].setSky(it.get(i).fcstValue);
                                break;
                            case "T1H":
                                weatherArr[index].setTemp(it.get(i).fcstValue);
                                break;
                            default:
                                continue;
                        }
                        index++;
                    }

                    for (int i = 0; i <= (weatherNumber - 1); i++)
                        weatherArr[i].setFcstTime(it.get(i).fcstTime);

                    // 리사이클러뷰 어댑터 설정
                    // 리사이클러 뷰에 데이터 연결
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            weatherRecyclerView.setAdapter(new WeatherAdapter(weatherArr));
                        }
                    });

                } else {

                }
            }

            // 응답 실패 시
            @Override
            public void onFailure(Call<WEATHER> call, Throwable t) {
                Log.e("API Failure", t.getMessage());

            }
        });
    }

    // 위치와 날씨 정보 요청
    // 내 현재 위치의 위경도를 격자 좌표로 변환하여 해당 위치의 날씨 정보 설정하기
    private void requestLocationAndWeather() {
        try {
            // 나의 현재 위치 요청
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(60 * 1000); // 요청 간격(1초)
            LocationCallback locationCallback = new LocationCallback() {
                // 요청 결과
                @Override
                public void onLocationResult(LocationResult p0) {
                    if (p0 != null) {
                        String address = "";
                        for (android.location.Location location : p0.getLocations()) {
                            // 현재 위치의 위경도를 격자 좌표로 변환
                            curPoint = new Common().dfs_xy_conv(location.getLatitude(), location.getLongitude());
                            // 오늘 날짜 텍스트뷰 설정
                            tvDate.setText(new SimpleDateFormat("MM월 dd일", Locale.getDefault()).format(Calendar.getInstance().getTime()) + "날씨");
                            // nx, ny지점의 날씨 가져와서 설정하기
                            setWeatherAndLocation(curPoint.x, curPoint.y);
                            address = getCurrentAddress(location.getLatitude(), location.getLongitude());
                        }
                        tvFSAddress.setText(address);
                    }
                }
            };

            // 위치 업데이트 요청
            // 내 위치 실시간으로 감지
            LocationServices.getFusedLocationProviderClient(getContext()).requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    // 퍼미션 요청 결과 처리
    @Override
    public void onRequestPermissionsResult(int permsRequestCode, @NonNull String[] permissions, @NonNull int[] grandResults) {
        super.onRequestPermissionsResult(permsRequestCode, permissions, grandResults);
        if (permsRequestCode == PERMISSIONS_REQUEST_CODE) {
            boolean allPermissionsGranted = true;
            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }

            if (allPermissionsGranted) {
                // 권한이 모두 부여된 경우 위치 정보 요청
                requestLocationAndWeather();
            } else {
                showToast("위치 권한이 거부되었습니다. 앱을 다시 실행하여 권한을 설정해주세요.");
            }
        }
    }

    // 런타임 퍼미션 체크
    private void checkRunTimePermission() {
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
            // 이미 권한이 부여된 경우 위치 정보 요청
            requestLocationAndWeather();
        } else {
            // 필요한 퍼미션 요청
            ActivityCompat.requestPermissions(getActivity(), REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
        }
    }

    // 위치 서비스 설정 다이얼로그 표시
    private void showDialogForLocationServiceSetting() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n" + "위치 설정을 수정하시겠습니까?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // 위치 설정으로 이동하는 인텐트 실행
                Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case GPS_ENABLE_REQUEST_CODE:
                if (checkLocationServicesStatus()) {
                    // GPS가 활성화되어 있으면 다시 퍼미션 확인
                    checkRunTimePermission();
                } else {
                    showToast("위치 서비스가 비활성화되었습니다. 위치 설정을 수정해주세요.");
                }
                break;
        }
    }

    // 위치 서비스 상태 확인
    private boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // Toast 메시지 표시
    private void showToast(String message) {
        if (!TextUtils.isEmpty(message)) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    public String getCurrentAddress(double latitude, double longitude) {
        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 7);

        } catch (IOException ioException) {
            /*     Toast.makeText(getContext(), "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();*/
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            /*    Toast.makeText(getContext(), "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();*/
            return "잘못된 GPS 좌표";
        }

        if (addresses == null || addresses.size() == 0) {
            /*    Toast.makeText(getContext(), "주소 미발견", Toast.LENGTH_LONG).show();*/
            return "주소 미발견";
        }

        Address address = addresses.get(0);
        return address.getAddressLine(0).toString() + "\n";
    }

    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Fragment가 연결될 때 Activity의 Context를 사용하여 Geocoder를 초기화
        geocoder = new Geocoder(context, Locale.getDefault());
    }
}
