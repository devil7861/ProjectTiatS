package com.jica.newpts.MainFragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.icu.text.SimpleDateFormat;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.jica.newpts.CommunityFragment.CommunitySearchActivity;
import com.jica.newpts.R;
import com.jica.newpts.TabLayoutActivity;
import com.jica.newpts.TotalLoginActivity;
import com.jica.newpts.ViewInfoPlantActivity;
import com.jica.newpts.beans.Board;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Response;

public class MainFragment extends Fragment implements View.OnClickListener {
    // 슬라이드 관련 변수
    boolean isPageOpenUpDown = false, isPageOpen = false;

    // 슬라이드 애니메이션
    Animation translateDownAnim, translateUpAnim, translateLeftAnim, translateRightAnim;

    // UI 요소
    ImageView ivCancel;
    TextView tvUserId, tvFMCountMyBoard, tvSettingTest, tvFMLogout, llFMLinearLayoutMessage;
    public TextView tvFMCurrentTimne, tvFMCurrentDay;
    Button btnFMPlantRegister, btnFMPlantRegisterCancel, btnFMHamburger;
    ConstraintLayout filter, loFMCommunity, loFMPlantManagement, slideingPageLayout, slidingPage01, slidingPage02;
    LinearLayout llFMLinearLayout, llFMMyBoard;

    // Firebase 및 데이터
    private FirebaseAuth firebaseAuth;
    private ArrayList<Board> arrayList = new ArrayList<>();

    // 시간 관련 포맷
    SimpleDateFormat simpleDate = new SimpleDateFormat("a hh:mm");
    SimpleDateFormat simpleDay = new SimpleDateFormat("yyyy.MM.dd");

    // 날짜 저장 변수
    String dateData = new String(), dateDay = new String();

    // 날씨 및 GPS 관련 변수
    private String base_date, base_time;
    private Point curPoint;

    // GPS 및 권한 관련 상수 및 변수
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    TextView tvFMCurrentAddress;
    Button btnFMRefresh, btnSelectPlant;

    // 날씨 정보 텍스트뷰
    TextView tvFMCurrentTemp, tvFMcurrentSky, tvFMRainType, tvFMHumid, tvFMStandardTime, tvFMRainRatio;

    // 날씨 아이콘
    ImageView ivFMSkyIcon;

    // 지오코딩 관련 변수
    private Geocoder geocoder;

    // Firestore 데이터베이스
    private FirebaseFirestore db;


    static int weatherNumber = 6; // 리사이클러뷰에서 보여줄 날씨예보 개수

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
      /*  // Geocoder 초기화
        geocoder = new Geocoder(requireActivity(), Locale.getDefault());*/


        Activity ownerActivity = requireActivity();

        btnFMHamburger = view.findViewById(R.id.btnFMHamburger);
        btnFMPlantRegister = view.findViewById(R.id.btnFMPlantRegister);
        btnFMPlantRegisterCancel = view.findViewById(R.id.btnFMPlantRegisterCancel);
        filter = view.findViewById(R.id.filter);
        firebaseAuth = FirebaseAuth.getInstance();
        ivCancel = view.findViewById(R.id.ivCancel);
        loFMCommunity = view.findViewById(R.id.loFMCommunity);
        slidingPage01 = view.findViewById(R.id.slidingPage01);

        slidingPage02 = view.findViewById(R.id.slidingPage02);
        tvFMLogout = view.findViewById(R.id.tvFMLogout);
        tvSettingTest = view.findViewById(R.id.tvSettingTest);
        tvUserId = view.findViewById(R.id.tvUserId);
        llFMLinearLayout = view.findViewById(R.id.llFMLinearLayout);
        llFMLinearLayoutMessage = view.findViewById(R.id.llFMLinearLayoutMessage);
        loFMPlantManagement = view.findViewById(R.id.loFMPlantManagement);
        tvFMCurrentTimne = view.findViewById(R.id.tvFMCurrentTimne);
        tvFMCurrentDay = view.findViewById(R.id.tvFMCurrentDay);
        // XML에서 UI 요소들을 참조
        btnFMRefresh = view.findViewById(R.id.btnFMRefresh);     // 새로고침 버튼
        tvFMCurrentAddress = view.findViewById(R.id.tvFMCurrentAddress);
        tvFMCurrentTemp = view.findViewById(R.id.tvFMCurrentTemp);
        tvFMcurrentSky = view.findViewById(R.id.tvFMcurrentSky);
        tvFMRainType = view.findViewById(R.id.tvFMRainType);
        tvFMHumid = view.findViewById(R.id.tvFMHumid);
        tvFMStandardTime = view.findViewById(R.id.tvFMStandardTime);
        ivFMSkyIcon = view.findViewById(R.id.ivFMSkyIcon);
        tvFMCountMyBoard = view.findViewById(R.id.tvFMCountMyBoard);
        llFMMyBoard = view.findViewById(R.id.llFMMyBoard);
        btnSelectPlant = view.findViewById(R.id.btnSelectPlant);

        // date 생성
        Date date = new Date();
        dateData = simpleDate.format(date);
        dateDay = simpleDay.format(date);


        // 화면 출력
        tvFMCurrentTimne.setText(String.valueOf(dateData));
        tvFMCurrentDay.setText(String.valueOf(dateDay));

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        boolean isLoggedIn = checkLoginStatus();
        updateUI(isLoggedIn, currentUser);

        //애니메이션
        translateLeftAnim = AnimationUtils.loadAnimation(ownerActivity, R.anim.translate_left);
        translateRightAnim = AnimationUtils.loadAnimation(ownerActivity, R.anim.translate_right);
        translateDownAnim = AnimationUtils.loadAnimation(ownerActivity, R.anim.translate_down);
        translateUpAnim = AnimationUtils.loadAnimation(ownerActivity, R.anim.translate_up);

        //애니메이션 리스너 설정
        MainFragment.SlidingPageAnimationListener animationListener = new MainFragment.SlidingPageAnimationListener();
        translateLeftAnim.setAnimationListener(animationListener);
        translateRightAnim.setAnimationListener(animationListener);

        //애니메이션 리스너 설정
        MainFragment.SlidingPageAnimationListener2 animationListener2 = new MainFragment.SlidingPageAnimationListener2();
        translateDownAnim.setAnimationListener(animationListener2);
        translateUpAnim.setAnimationListener(animationListener2);

        // 위치 서비스 상태 확인
        if (!checkLocationServicesStatus()) {
            // 위치 서비스가 비활성화되어 있으면 설정 다이얼로그 표시
            showDialogForLocationServiceSetting();
        } else {
            // 위치 서비스가 활성화되어 있으면 런타임 퍼미션 확인
            checkRunTimePermission();
        }

        // 위치와 날씨 정보 요청
        requestLocationAndWeather();

        tvUserId.setOnClickListener(this);
        loFMCommunity.setOnClickListener(this);
        tvFMLogout.setOnClickListener(this);
        ivCancel.setOnClickListener(this);
        btnFMHamburger.setOnClickListener(this);
        btnFMPlantRegister.setOnClickListener(this);
        btnFMPlantRegisterCancel.setOnClickListener(this);
        loFMPlantManagement.setOnClickListener(this);
        btnFMRefresh.setOnClickListener(this);
        llFMMyBoard.setOnClickListener(this);
        btnSelectPlant.setOnClickListener(this);

    }

    //애니메이션 리스너
    private class SlidingPageAnimationListener implements Animation.AnimationListener {
        @Override
        public void onAnimationEnd(Animation animation) {
            //슬라이드 열기->닫기
            if (isPageOpen) {
                btnFMHamburger.setVisibility(View.VISIBLE);
                // -----------------------------------------------
                // slidingPage01이 gone이될때 자동으로 하위내용도 gone으로 바꾸는 부분
                // slidingPage01 레이아웃 내의 모든 하위 뷰를 순회하며 GONE으로 설정
                for (int i = 0; i < slidingPage01.getChildCount(); i++) {
                    View childView = slidingPage01.getChildAt(i);
                    childView.setVisibility(View.GONE);
                }
                // -----------------------------------------------
                /*        slidingPage01.setVisibility(View.GONE);*/
                btnFMPlantRegister.setClickable(true);
                isPageOpen = false;
            }
            //슬라이드 닫기->열기
            else {
                btnFMHamburger.setVisibility(View.INVISIBLE);
                btnFMPlantRegister.setClickable(false);
                isPageOpen = true;
            }

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }

        @Override
        public void onAnimationStart(Animation animation) {

        }
    }

    //애니메이션 리스너
    private class SlidingPageAnimationListener2 implements Animation.AnimationListener {
        @Override
        public void onAnimationEnd(Animation animation) {

            //슬라이드 열기->닫기
            if (isPageOpenUpDown) {
                // slidingPage02 레이아웃 내의 모든 하위 뷰를 순회하며 GONE으로 설정
                for (int i = 0; i < slidingPage02.getChildCount(); i++) {
                    View childView = slidingPage02.getChildAt(i);
                    childView.setVisibility(View.GONE);
                }

                /*    slidingPage02.setVisibility(View.GONE);*/

                btnFMPlantRegister.setClickable(true);

                isPageOpenUpDown = false;
            }
            //슬라이드 닫기->열기
            else {

                btnFMPlantRegister.setClickable(false);

                isPageOpenUpDown = true;
            }


        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }

        @Override
        public void onAnimationStart(Animation animation) {

        }
    }


    @Override
    public void onClick(View view) {
        int clickedViewId = view.getId();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (clickedViewId == R.id.tvUserId) {
            boolean isLoggedIn = checkLoginStatus();

            if (!isLoggedIn) {
                requireLogin();
            } else {
                /*navigateToFragment(new ProfileFragment(), R.id.tab5);*/
                Intent intent = new Intent(requireContext(), TabLayoutActivity.class);
                intent.putExtra("sendData", "MainFragmentToProfile");
                startActivity(intent);
            }
        } else if (clickedViewId == R.id.loFMCommunity) {
            navigateToFragment(new CommunityFragment(), R.id.tab4);
        } else if (clickedViewId == R.id.tvFMLogout) {
            handleLogoutClick();
        } else if (clickedViewId == R.id.ivCancel) {
            toggleSideBar();
        } else if (clickedViewId == R.id.btnFMHamburger) {
            toggleSideBar();
        } else if (clickedViewId == R.id.btnFMPlantRegister) {
            // 로그인 여부를 확인하는 코드를 추가해야 합니다.
            boolean isLoggedIn = checkLoginStatus(); // 이 부분은 실제 로그인 상태 확인 로직을 구현해야 합니다.
            if (!isLoggedIn) {
                requireLogin();

            } else {
                togglePlantRegister();
            }
        } else if (clickedViewId == R.id.btnFMPlantRegisterCancel) {
            togglePlantRegister();
        } else if (clickedViewId == R.id.loFMPlantManagement) {
            // 로그인 여부를 확인하는 코드를 추가해야 합니다.
            boolean isLoggedIn = checkLoginStatus(); // 이 부분은 실제 로그인 상태 확인 로직을 구현해야 합니다.
            if (!isLoggedIn) {
                requireLogin();
            } else {
                navigateToFragment(new PlantManagementFragment(), R.id.tab2);
            }
        } else if (clickedViewId == R.id.btnFMRefresh) {
            requestLocationAndWeather();
        } else if (clickedViewId == R.id.llFMMyBoard) {
            Intent intent = new Intent(getActivity(), CommunitySearchActivity.class);
            intent.putExtra("myBoard", currentUser.getEmail());
            startActivity(intent);
        } else if (clickedViewId == R.id.btnSelectPlant) {
            Intent intent = new Intent(getActivity(), ViewInfoPlantActivity.class);
            startActivity(intent);
        }
    }

    private void handleLogoutClick() {
        firebaseAuth.signOut();
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
        builder.setTitle("로그인페이지로 이동");
        builder.setIcon(android.R.drawable.ic_dialog_alert); //안드로이드에서 제공하는 아이콘 이미지 사용
        builder.setMessage("로그인페이지로 이동하시겠습니까??");

        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(requireContext(), TotalLoginActivity.class);
                startActivity(intent);
            }
        });    //긍정 버튼 - BUTTON_POSITIVE (-1)
        //builder.setNeutralButton("확인", null);   //확인 버튼 - BUTTON_NEUTRAL (-3)
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(requireContext(), TabLayoutActivity.class);
                startActivity(intent);
            }
        }); //부정 버튼 -  BUTTON_NEGATIVE (-2)

        //대화상자가 보여진 이후에는 반드시 대화상자의 버튼으로만 대화상자가 종료하도록 한다.
        builder.setCancelable(false);

        //대화상자 만들기
        androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        // 다이얼로그가 나타날 때 배경을 반투명하게 설정


        alertDialog.show(); //대화상자 보이기


    }

    private void navigateToFragment(Fragment fragment, int selectedTabId) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);

        BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(selectedTabId);

        transaction.commit();
    }

    private boolean checkLoginStatus() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        return currentUser != null;
    }

    private void updateUI(boolean isLoggedIn, FirebaseUser currentUser) {
        if (!isLoggedIn) {
            tvUserId.setText("로그인을 해주세요");
            tvFMLogout.setVisibility(View.GONE);
            llFMLinearLayout.setVisibility(View.GONE);
            llFMLinearLayoutMessage.setVisibility(View.VISIBLE);
        } else {
            tvUserId.setText(currentUser.getEmail());
            countMyBoard();
        }
    }

    private void toggleSideBar() {
        if (isPageOpen) {
            //애니메이션 시작
            slidingPage01.startAnimation(translateRightAnim);
            filter.setVisibility(View.GONE);
        }
        //열기
        else {
            filter.setVisibility(View.VISIBLE);
            // ------------------------------------------------------------------
            // slidingPage01이 gone이될때 자동으로 하위내용도 gone으로 바꾸는 부분
            // slidingPage01 레이아웃 내의 모든 하위 뷰를 순회하며 GONE으로 설정
            for (int i = 0; i < slidingPage01.getChildCount(); i++) {
                View childView = slidingPage01.getChildAt(i);
                childView.setVisibility(View.VISIBLE);
            }
            // -------------------------------------------------------------------
            slidingPage01.setVisibility(View.VISIBLE);
            slidingPage01.startAnimation(translateLeftAnim);
        }
    }

    private void togglePlantRegister() {
        if (isPageOpenUpDown) {
            //애니메이션 시작
            btnFMHamburger.setClickable(true);
            btnFMPlantRegister.setClickable(true);
            slidingPage02.startAnimation(translateUpAnim);

        }
        //열기
        else {
            btnFMHamburger.setClickable(false);
            btnFMPlantRegister.setClickable(false);
            // slidingPage02 레이아웃 내의 모든 하위 뷰를 순회하며 GONE으로 설정
            for (int i = 0; i < slidingPage02.getChildCount(); i++) {
                View childView = slidingPage02.getChildAt(i);
                childView.setVisibility(View.VISIBLE);
            }
            slidingPage02.setVisibility(View.VISIBLE);
            slidingPage02.startAnimation(translateDownAnim);
        }
    }

    // 날씨 정보와 위치 정보 설정
    private void setWeatherAndLocation(int nx, int ny) {
        // 준비 단계 : base_date(발표 일자), base_time(발표 시각)
        // 현재 날짜, 시간 정보 가져오기
        Calendar cal = Calendar.getInstance();
        base_date = new java.text.SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(cal.getTime()); // 현재 날짜
        String timeH = new java.text.SimpleDateFormat("HH", Locale.getDefault()).format(cal.getTime());// 현재 시각
        String timeM = new java.text.SimpleDateFormat("mm", Locale.getDefault()).format(cal.getTime()); // 현재 분
        base_time = new Common().getBaseTime(timeH, timeM);

        // 현재 시각이 00시이고 45분 이하여서 baseTime이 2330이면 어제 정보 받아오기
        if ("00".equals(timeH) && "2330".equals(base_time)) {
            cal.add(Calendar.DATE, -1);
            base_date = new java.text.SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(cal.getTime());
        }

        // API를 통해 날씨 정보 요청
        // (한 페이지 결과 수 = 60, 페이지 번호 = 1, 응답 자료 형식-"JSON", 발표 날싸, 발표 시각, 예보지점 좌표)
        Call<WEATHER> call = ApiObject.getRetrofitService().GetWeather(60, 1, "JSON", base_date, base_time, String.valueOf(nx), String.valueOf(ny));

        // 비동기적으로 실행하기
        call.enqueue(new retrofit2.Callback<WEATHER>() {
            // 응답 성공 시
            @Override
            public void onResponse(Call<WEATHER> call, Response<WEATHER> response) {
                if (response.isSuccessful()) {
                    // 날씨 정보 가져오기
                    List<ITEM> it = response.body().response.body.items.item;

                    // 현재 시각부터 1시간 뒤의 날씨 6개를 담을 배열
                    /*    ModelWeather[] weatherArr = {new ModelWeather(), new ModelWeather(), new ModelWeather(), new ModelWeather(), new ModelWeather(), new ModelWeather()};*/
                    ModelWeather[] weatherArr = new ModelWeather[6];
                    for (int i = 0; i < weatherArr.length; i++) {
                        weatherArr[i] = new ModelWeather();
                    }
                    // 배열 채우기
                    int index = 0;
                    int totalCount = response.body().response.body.totalCount - 1;
                    for (int i = 0; i <= totalCount; i++) {
                        index %= 6;
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

                    for (int i = 0; i <= (weatherNumber - 1); i++) {
                        weatherArr[i].setFcstTime(it.get(i).fcstTime);
                    }

                    String result = "";
                    switch (weatherArr[0].getRainType()) {
                        case "0":
                            result = "없음";
                            break;
                        case "1":
                            result = "비";
                            ivFMSkyIcon.setImageResource(R.drawable.weather_rain);
                            break;
                        case "2":
                            result = "비/눈";
                            ivFMSkyIcon.setImageResource(R.drawable.weather_rainsnow);
                            break;
                        case "3":
                            result = "눈";
                            ivFMSkyIcon.setImageResource(R.drawable.weather_snow);
                            break;
                        case "4":
                            result = "소나기";
                            ivFMSkyIcon.setImageResource(R.drawable.weather_rain);
                            break;
                        case "5":
                            result = "빗방울";
                            ivFMSkyIcon.setImageResource(R.drawable.weather_drop);
                            break;
                        case "6":
                            result = "빗방울/눈날림";
                            ivFMSkyIcon.setImageResource(R.drawable.weather_rainsnow);
                            break;
                        case "7":
                            result = "눈날림";
                            ivFMSkyIcon.setImageResource(R.drawable.weather_blizzard);
                            break;
                        default:
                            result = "오류 rainType: " + weatherArr[0].getRainType();
                            break;
                    }
                    tvFMRainType.setText("RainType : " + result);
                    String skyResult = "";
                    switch (weatherArr[0].getSky()) {
                        case "1":
                            skyResult = "맑음";
                            ivFMSkyIcon.setImageResource(R.drawable.weather_sunny);
                            break;
                        case "3":
                            skyResult = "구름 많음";
                            ivFMSkyIcon.setImageResource(R.drawable.weather_cloud);
                            break;
                        case "4":
                            skyResult = "흐림";
                            ivFMSkyIcon.setImageResource(R.drawable.weather_cloud);
                            break;
                        default:
                            skyResult = "오류 sky: " + weatherArr[0].getSky();
                            break;
                    }
                    tvFMcurrentSky.setText(skyResult);


                    tvFMCurrentTemp.setText(weatherArr[0].getTemp() + "℃");


                    tvFMHumid.setText("습도 : " + weatherArr[0].getHumidity() + "%");
                    tvFMStandardTime.setText(weatherArr[0].getFcstTime().substring(0, 2) + "시 기준");

                }
            }

            // 응답 실패 시
            @Override
            public void onFailure(Call<WEATHER> call, Throwable t) {
            /*    TextView tvError = getActivity().findViewById(R.id.tvError);
                tvError.setText("api fail : " + t.getMessage() + "\n 다시 시도해주세요.");
                tvError.setVisibility(View.VISIBLE);*/
                Log.d("api fail", t.getMessage());
            }
        });
    }

    // 위치와 날씨 정보 요청
    // 내 현재 위치의 위경도를 격자 좌표로 변환하여 해당 위치의 날씨정보 설정하기
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
                        String extractedText = "";
                        for (android.location.Location location : p0.getLocations()) {
                            // 현재 위치의 위경도를 격자 좌표로 변환
                            curPoint = new Common().dfs_xy_conv(location.getLatitude(), location.getLongitude());
                            // 오늘 날짜 텍스트뷰 설정
                            //tvDate.setText(new java.text.SimpleDateFormat("MM월 dd일", Locale.getDefault()).format(Calendar.getInstance().getTime()) + "날씨");
                            // nx, ny지점의 날씨 가져와서 설정하기
                            setWeatherAndLocation(curPoint.x, curPoint.y);
                            String address = getCurrentAddress(location.getLatitude(), location.getLongitude());


                            String input = address;
                            String[] words = input.split(" "); // 띄어쓰기를 기준으로 문자열을 나눕니다.

                            if (words.length >= 3) {
                                // 뒤에서 첫 번째 띄어쓰기와 두 번째 띄어쓰기 사이의 문자를 추출합니다.
                                extractedText = words[words.length - 4];
                                extractedText += " " + words[words.length - 2];
                                System.out.println(extractedText);
                            }

                        }
                        tvFMCurrentAddress.setText(extractedText);
                    }
                }
            };

            // 위치 업데이트 요청
            // 내 위치 실시간으로 감지
            LocationServices.getFusedLocationProviderClient(requireContext()).requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    // 퍼미션 요청 결과 처리
    @Override
    public void onRequestPermissionsResult(int permsRequestCode, @NonNull String[] permissions, @NonNull int[] grandResults) {
        super.onRequestPermissionsResult(permsRequestCode, permissions, grandResults);
        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {
            boolean check_result = true;
            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            if (!check_result) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), REQUIRED_PERMISSIONS[1])) {
                    /*  Toast.makeText(getContext(), "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();*/
                    getActivity().finish();
                } else {
                    /*Toast.makeText(getActivity(), "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();*/
                }
            }
        }
    }

    // 런타임 퍼미션 체크
    void checkRunTimePermission() {
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
            return;
        } else {
            // 필요한 퍼미션 요청
            ActivityCompat.requestPermissions(getActivity(), REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
        }
    }

    // 현재 위치의 주소 정보 가져오기
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
                    if (checkLocationServicesStatus()) {
                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                        checkRunTimePermission();
                        return;
                    }
                }
                break;
        }
    }

    // 위치 서비스 상태 확인
    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void onResume() {
        super.onResume();
        requestLocationAndWeather();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Fragment가 연결될 때 Activity의 Context를 사용하여 Geocoder를 초기화
        geocoder = new Geocoder(context, Locale.getDefault());
    }

    public String getTvFMCurrentAddressText() {
        return tvFMCurrentAddress.getText().toString();
    }

    public void countMyBoard() {
        db = FirebaseFirestore.getInstance(); // Firestore 초기화
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        db.collection("Board")
                .orderBy("f_board_idx", Query.Direction.DESCENDING)
                .whereEqualTo("f_user", currentUser.getEmail())
                .whereEqualTo("f_del", false)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        arrayList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Board board = document.toObject(Board.class);
                            arrayList.add(board);
                        }
                        tvFMCountMyBoard.setText(arrayList.size() + "개");

                    } else {
                        Log.e("TAG", "Error getting documents: ", task.getException());
                    }
                });
    }

    public void requireLogin() {

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
        builder.setTitle("로그인이 필요합니다");
        builder.setIcon(android.R.drawable.ic_dialog_alert); //안드로이드에서 제공하는 아이콘 이미지 사용
        builder.setMessage("로그인이 필요한 행동입니다\n계속하시겠습니까?");

        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(requireContext(), TotalLoginActivity.class);
                startActivity(intent);
            }
        });    //긍정 버튼 - BUTTON_POSITIVE (-1)
        //builder.setNeutralButton("확인", null);   //확인 버튼 - BUTTON_NEUTRAL (-3)
        builder.setNegativeButton("아니오", null); //부정 버튼 -  BUTTON_NEGATIVE (-2)

        //대화상자가 보여진 이후에는 반드시 대화상자의 버튼으로만 대화상자가 종료하도록 한다.
        builder.setCancelable(false);

        //대화상자 만들기
        androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        // 다이얼로그가 나타날 때 배경을 반투명하게 설정


        alertDialog.show(); //대화상자 보이기
    }
}

