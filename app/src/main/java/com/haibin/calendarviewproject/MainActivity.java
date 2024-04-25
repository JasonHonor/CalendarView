package com.haibin.calendarviewproject;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarLayout;
import com.haibin.calendarview.CalendarView;
import com.haibin.calendarview.TrunkBranchAnnals;
import com.haibin.calendarviewproject.base.activity.BaseActivity;
import com.haibin.calendarviewproject.colorful.ColorfulActivity;
import com.haibin.calendarviewproject.custom.CustomActivity;
import com.haibin.calendarviewproject.full.FullActivity;
import com.haibin.calendarviewproject.index.IndexActivity;
import com.haibin.calendarviewproject.meizu.MeiZuActivity;
import com.haibin.calendarviewproject.meizu.MeiZuMonthView;
import com.haibin.calendarviewproject.meizu.MeizuWeekView;
import com.haibin.calendarviewproject.mix.MixActivity;
import com.haibin.calendarviewproject.multi.MultiActivity;
import com.haibin.calendarviewproject.pager.ViewPagerActivity;
import com.haibin.calendarviewproject.progress.ProgressActivity;
import com.haibin.calendarviewproject.range.RangeActivity;
import com.haibin.calendarviewproject.simple.SimpleActivity;
import com.haibin.calendarviewproject.single.SingleActivity;
import com.haibin.calendarviewproject.solay.SolarActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class MainActivity extends BaseActivity implements
        CalendarView.OnCalendarSelectListener,
        CalendarView.OnCalendarLongClickListener,
        CalendarView.OnMonthChangeListener,
        CalendarView.OnYearChangeListener,
        CalendarView.OnWeekChangeListener,
        CalendarView.OnViewChangeListener,
        CalendarView.OnCalendarInterceptListener,
        CalendarView.OnYearViewChangeListener,
        DialogInterface.OnClickListener,
        View.OnClickListener {

    TextView mTextMonthDay;

    TextView mTextYear;

    TextView mTextLunar;

    TextView mTextCurrentDay;

    CalendarView mCalendarView;

    RelativeLayout mRelativeTool;
    private int mYear;
    CalendarLayout mCalendarLayout;

    private AlertDialog mMoreDialog;

    private AlertDialog mFuncDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initView() {



        setStatusBarDarkMode();
        mTextMonthDay = findViewById(R.id.tv_month_day);
        mTextYear = findViewById(R.id.tv_year);
        mTextLunar = findViewById(R.id.tv_lunar);

        mRelativeTool = findViewById(R.id.rl_tool);
        mCalendarView = findViewById(R.id.calendarView);

        //mCalendarView.setRange(2018, 7, 1, 2019, 4, 28);
        mTextCurrentDay = findViewById(R.id.tv_current_day);
        mTextMonthDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mCalendarLayout.isExpand()) {
                    mCalendarLayout.expand();
                    return;
                }
                mCalendarView.showYearSelectLayout(mYear);
                mTextLunar.setVisibility(View.GONE);
                mTextYear.setVisibility(View.GONE);
                mTextMonthDay.setText(String.valueOf(mYear));
            }
        });
        findViewById(R.id.iv_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMoreDialog == null) {
                    mMoreDialog = new AlertDialog.Builder(MainActivity.this)
                            .setTitle(R.string.list_dialog_title)
                            .setItems(R.array.list_dialog_items, MainActivity.this)
                            .create();
                }
                mMoreDialog.show();
            }
        });

        final DialogInterface.OnClickListener listener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                mCalendarLayout.expand();
                                break;
                            case 1:
                                boolean result = mCalendarLayout.shrink();
                                Log.e("shrink", " --  " + result);
                                break;
                            case 2:
                                mCalendarView.scrollToPre(false);
                                break;
                            case 3:
                                mCalendarView.scrollToNext(false);
                                break;
                            case 4:
                                //mCalendarView.scrollToCurrent(true);
                                mCalendarView.scrollToCalendar(2018, 12, 30);
                                break;
                            case 5:
                                mCalendarView.setRange(2018, 7, 1, 2019, 4, 28);
//                                mCalendarView.setRange(mCalendarView.getCurYear(), mCalendarView.getCurMonth(), 6,
//                                        mCalendarView.getCurYear(), mCalendarView.getCurMonth(), 23);
                                break;
                            case 6:
                                Log.e("scheme", "  " + mCalendarView.getSelectedCalendar().getScheme() + "  --  "
                                        + mCalendarView.getSelectedCalendar().isCurrentDay());
                                List<Calendar> weekCalendars = mCalendarView.getCurrentWeekCalendars();
                                for (Calendar calendar : weekCalendars) {
                                    Log.e("onWeekChange", calendar.toString() + "  --  " + calendar.getScheme());
                                }
                                new AlertDialog.Builder(MainActivity.this)
                                        .setMessage(String.format("Calendar Range: \n%s —— %s",
                                                mCalendarView.getMinRangeCalendar(),
                                                mCalendarView.getMaxRangeCalendar()))
                                        .show();
                                break;
                        }
                    }
                };

        findViewById(R.id.iv_func).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFuncDialog == null) {
                    mFuncDialog = new AlertDialog.Builder(MainActivity.this)
                            .setTitle(R.string.func_dialog_title)
                            .setItems(R.array.func_dialog_items, listener)
                            .create();
                }
                mFuncDialog.show();
            }
        });

        mCalendarLayout = findViewById(R.id.calendarLayout);
        mCalendarView.setOnYearChangeListener(this);
        mCalendarView.setOnCalendarSelectListener(this);
        mCalendarView.setOnMonthChangeListener(this);
        mCalendarView.setOnCalendarLongClickListener(this, true);
        mCalendarView.setOnWeekChangeListener(this);
        mCalendarView.setOnYearViewChangeListener(this);

        //设置日期拦截事件，仅适用单选模式，当前无效
        mCalendarView.setOnCalendarInterceptListener(this);

        mCalendarView.setOnViewChangeListener(this);
        mTextYear.setText(String.valueOf(mCalendarView.getCurYear()));
        mYear = mCalendarView.getCurYear();
        mTextMonthDay.setText(mCalendarView.getCurMonth() + "月" + mCalendarView.getCurDay() + "日");
        mTextLunar.setText("今日");
        mTextCurrentDay.setText(String.valueOf(mCalendarView.getCurDay()));
    }

    // 下载公共节假日数据
    class CalendarDay {
        public String date;
        public String name;
        public boolean isOffDay;
    }

    class CalendarList {
        public Map<String, CalendarDay> mHolidays;
        public Map<String, CalendarDay> mWeekends;
        public Map<String, CalendarDay> mWorkDays;
    }

    public class CalendarSyncThread extends Thread {
        private String mYear;
        private String mType;
        private Map<String, CalendarDay> mHolidays;
        public CalendarSyncThread(String type, String year) {
            mYear = year;
            mType =  type;
        }

        private void SaveFile(String sFile,String sContent) {
            try {
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    FileOutputStream fos = new FileOutputStream(Environment.getExternalStorageDirectory().getCanonicalPath() + "/calendar/"+sFile);
                    fos.write(sContent.getBytes());
                    fos.close();
                }
            }catch(Exception e) {
                e.printStackTrace();
            }
        }

        private String LoadCache() {

            String sCachedData = "";
            try{
                //check directory
                File fds = new File(Environment.getExternalStorageDirectory().getCanonicalPath()+"/calendar/");
                if(!fds.exists()) {
                    fds.mkdirs();
                    return "";
                }

                StringBuilder sb  = new StringBuilder();
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    //检查本地存储是否已经缓存了数据
                    File fis = new File(Environment.getExternalStorageDirectory().getCanonicalPath()+"/calendar/cache-" + mType + "-" + mYear);
                    if (fis.exists() && fis.canRead()) {
                        if (fis.length() < 1024 * 1024) {
                            FileInputStream fin = new FileInputStream(fis);

                            byte[] tmp = new byte[1024];
                            int len = 0;
                            while ((len = fin.read(tmp)) > 0) {
                                sb.append(new String(tmp, 0, len));
                            }
                            sCachedData = sb.toString();
                            fin.close();
                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return sCachedData;
        }
        @Override
        public void run() {

            String sCachedData = LoadCache();

            if(sCachedData.isEmpty()) {
                try {
                    //String url = "http://opendata.baidu.com/api.php?query=2020年5月&resource_id=6018&format=json";
                    String url = "https://api.jiejiariapi.com/v1/" + mType + "/" + mYear;
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().url(url).build();
                    okhttp3.Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String sResponse = response.body().string();
                        if (sResponse.isEmpty()) {
                            Log.e("DEMO", "okHttp is no response");
                            return;
                        }
                        SaveFile("cache-" + mType + "-" + mYear, sResponse);
                        //解析特殊的返回值结构 {"2022-12-31":{"date":"2022-12-31","name":"元旦","isOffDay":true},...}
                        sCachedData = sResponse;
                        //Log.i("DEMO", response.body().string());
                    } else {
                        Log.e("DEMO", "okHttp is request error");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            mHolidays = JSONObject.parseObject(sCachedData, new TypeReference<Map<String, CalendarDay>>() {
            });
            mHolidays = sortByKey(mHolidays, false);
        }

        public Map<String, CalendarDay> GetCalenderDays() {
            return mHolidays;
        }
    }

    public static <K extends Comparable<? super K>, V> Map<K, V> sortByKey(Map<K, V> map, boolean isDesc) {
        Map<K, V> result = new LinkedHashMap<K,V>();
        if (isDesc) {
            map.entrySet().stream().sorted(Map.Entry.<K, V>comparingByKey().reversed())
                    .forEachOrdered(e -> result.put(e.getKey(), e.getValue()));
        } else {
            map.entrySet().stream().sorted(Map.Entry.<K, V>comparingByKey())
                    .forEachOrdered(e -> result.put(e.getKey(), e.getValue()));
        }
        return result;
    }

    private CalendarList okHttp_SyncGet(String year) {
        CalendarList calendarList = new CalendarList();

        CalendarSyncThread thread1 = new CalendarSyncThread("holidays",year);
        thread1.start();
        CalendarSyncThread thread2 = new CalendarSyncThread("weekends",year);
        thread2.start();
        CalendarSyncThread thread3 = new CalendarSyncThread("workdays",year);
        thread3.start();
        try {
            thread1.join();
            thread2.join();
            thread3.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        calendarList.mHolidays = thread1.GetCalenderDays();
        calendarList.mWeekends = thread2.GetCalenderDays();
        calendarList.mWorkDays = thread3.GetCalenderDays();

        //合并数据
        return calendarList;
    }

    private void mergeCalendarDays(Map<String, CalendarDay> holidays,Map<String, CalendarDay> weekends,Map<String,CalendarDay> workdays)
    {
        //数据合并：holiday+workdays+weekends
    }

    @SuppressWarnings("unused")
    @Override
    protected void initData() {

        final int year = mCalendarView.getCurYear();
        final int month = mCalendarView.getCurMonth();

        Map<String, Calendar> map = new HashMap<>();
        for (int y = 2007 ; y < year+1; y++) {
            CalendarList calendarList = okHttp_SyncGet(String.format("%d",y));
            if(calendarList.mWorkDays==null || calendarList.mWeekends==null || calendarList.mHolidays==null)
            {
                continue;
            }
            for (int m = 1; m <= 12; m++) {
                //计算节假日，并填写标记
                //https://api.jiejiariapi.com/v1/holidays/2024
                //https://api.jiejiariapi.com/v1/weekends/2024
                //https://api.jiejiariapi.com/v1/workdays/2024
                for(int d = 1;d < 31; d++) {
                    String sKey = String.format("%d-%02d-%02d",y,m,d);

                    //是否为周末
                    boolean bIsWeekendDay =false;

                    //是否为休息日
                    boolean bIsRestDay=false;

                    //是否为假日
                    boolean bIsHoliday=false;

                    if(calendarList.mWeekends.get(sKey)!=null) {
                        bIsWeekendDay=true;
                    }

                    if(calendarList.mHolidays!=null && calendarList.mHolidays.containsKey(sKey)) {
                        CalendarDay holiday = calendarList.mHolidays.get(sKey);
                        if(holiday!=null && holiday.isOffDay) {
                            map.put(getSchemeCalendar(y, m, d, 0xFF40db25, "休").toString(),
                                    getSchemeCalendar(y, m, d, 0xFF40db25, "休"));
                            bIsHoliday=true;
                        }
                    }else {
                        //不是工作日，即为休息日
                        if(calendarList.mWorkDays.get(sKey)==null) {
                            bIsRestDay=true;
                        }
                    }

                    if(bIsHoliday) {
                        map.put(getSchemeCalendar(y, m, d, 0xFF40db25, "休").toString(),
                                getSchemeCalendar(y, m, d, 0xFF40db25, "休"));
                    }
                    //周末，非休息日，则为工作日
                    else if(bIsWeekendDay && bIsRestDay) {
                        map.put(getSchemeCalendar(y, m, d, 0xFF40db25, "休").toString(),
                                getSchemeCalendar(y, m, d, 0xFF40db25, "休"));
                    }else {
                        if(bIsWeekendDay) {
                            map.put(getSchemeCalendar(y, m, d, 0xFFf9a6fa, "班").toString(),
                                    getSchemeCalendar(y, m, d, 0xFFf9a6fa, "班"));
                        }
                    }
                }
/*
                map.put(getSchemeCalendar(y, m, 1, 0xFF40db25, "假").toString(),
                        getSchemeCalendar(y, m, 1, 0xFF40db25, "假"));
                map.put(getSchemeCalendar(y, m, 2, 0xFFe69138, "游").toString(),
                        getSchemeCalendar(y, m, 2, 0xFFe69138, "游"));
                map.put(getSchemeCalendar(y, m, 3, 0xFFdf1356, "事").toString(),
                        getSchemeCalendar(y, m, 3, 0xFFdf1356, "事"));
                map.put(getSchemeCalendar(y, m, 4, 0xFFaacc44, "车").toString(),
                        getSchemeCalendar(y, m, 4, 0xFFaacc44, "车"));
                map.put(getSchemeCalendar(y, m, 5, 0xFFbc13f0, "驾").toString(),
                        getSchemeCalendar(y, m, 5, 0xFFbc13f0, "驾"));
                map.put(getSchemeCalendar(y, m, 6, 0xFF542261, "记").toString(),
                        getSchemeCalendar(y, m, 6, 0xFF542261, "记"));
                map.put(getSchemeCalendar(y, m, 7, 0xFF4a4bd2, "会").toString(),
                        getSchemeCalendar(y, m, 7, 0xFF4a4bd2, "会"));
                map.put(getSchemeCalendar(y, m, 8, 0xFFe69138, "车").toString(),
                        getSchemeCalendar(y, m, 8, 0xFFe69138, "车"));
                map.put(getSchemeCalendar(y, m, 9, 0xFF542261, "考").toString(),
                        getSchemeCalendar(y, m, 9, 0xFF542261, "考"));
                map.put(getSchemeCalendar(y, m, 10, 0xFF87af5a, "记").toString(),
                        getSchemeCalendar(y, m, 10, 0xFF87af5a, "记"));
                map.put(getSchemeCalendar(y, m, 11, 0xFF40db25, "会").toString(),
                        getSchemeCalendar(y, m, 11, 0xFF40db25, "会"));
                map.put(getSchemeCalendar(y, m, 12, 0xFFcda1af, "游").toString(),
                        getSchemeCalendar(y, m, 12, 0xFFcda1af, "游"));
                map.put(getSchemeCalendar(y, m, 13, 0xFF95af1a, "事").toString(),
                        getSchemeCalendar(y, m, 13, 0xFF95af1a, "事"));
                map.put(getSchemeCalendar(y, m, 14, 0xFF33aadd, "学").toString(),
                        getSchemeCalendar(y, m, 14, 0xFF33aadd, "学"));
                map.put(getSchemeCalendar(y, m, 15, 0xFF1aff1a, "码").toString(),
                        getSchemeCalendar(y, m, 15, 0xFF1aff1a, "码"));
                map.put(getSchemeCalendar(y, m, 16, 0xFF22acaf, "驾").toString(),
                        getSchemeCalendar(y, m, 16, 0xFF22acaf, "驾"));
                map.put(getSchemeCalendar(y, m, 17, 0xFF99a6fa, "校").toString(),
                        getSchemeCalendar(y, m, 17, 0xFF99a6fa, "校"));
                map.put(getSchemeCalendar(y, m, 18, 0xFFe69138, "车").toString(),
                        getSchemeCalendar(y, m, 18, 0xFFe69138, "车"));
                map.put(getSchemeCalendar(y, m, 19, 0xFF40db25, "码").toString(),
                        getSchemeCalendar(y, m, 19, 0xFF40db25, "码"));
                map.put(getSchemeCalendar(y, m, 20, 0xFFe69138, "火").toString(),
                        getSchemeCalendar(y, m, 20, 0xFFe69138, "火"));
                map.put(getSchemeCalendar(y, m, 21, 0xFF40db25, "假").toString(),
                        getSchemeCalendar(y, m, 21, 0xFF40db25, "假"));
                map.put(getSchemeCalendar(y, m, 22, 0xFF99a6fa, "记").toString(),
                        getSchemeCalendar(y, m, 22, 0xFF99a6fa, "记"));
                map.put(getSchemeCalendar(y, m, 23, 0xFF33aadd, "假").toString(),
                        getSchemeCalendar(y, m, 23, 0xFF33aadd, "假"));
                map.put(getSchemeCalendar(y, m, 24, 0xFF40db25, "校").toString(),
                        getSchemeCalendar(y, m, 24, 0xFF40db25, "校"));
                map.put(getSchemeCalendar(y, m, 25, 0xFF1aff1a, "假").toString(),
                        getSchemeCalendar(y, m, 25, 0xFF1aff1a, "假"));
                map.put(getSchemeCalendar(y, m, 26, 0xFF40db25, "议").toString(),
                        getSchemeCalendar(y, m, 26, 0xFF40db25, "议"));
                map.put(getSchemeCalendar(y, m, 27, 0xFF95af1a, "假").toString(),
                        getSchemeCalendar(y, m, 27, 0xFF95af1a, "假"));
                map.put(getSchemeCalendar(y, m, 28, 0xFF40db25, "码").toString(),
                        getSchemeCalendar(y, m, 28, 0xFF40db25, "码"));
 */
            }
        }

        //28560 数据量增长不会影响UI响应速度，请使用这个API替换
        mCalendarView.setSchemeDate(map);

        //可自行测试性能差距
        //mCalendarView.setSchemeDate(schemes);

        findViewById(R.id.ll_flyme).setOnClickListener(this);
        findViewById(R.id.ll_simple).setOnClickListener(this);
        findViewById(R.id.ll_range).setOnClickListener(this);
        findViewById(R.id.ll_mix).setOnClickListener(this);
        findViewById(R.id.ll_colorful).setOnClickListener(this);
        findViewById(R.id.ll_index).setOnClickListener(this);
        findViewById(R.id.ll_tab).setOnClickListener(this);
        findViewById(R.id.ll_single).setOnClickListener(this);
        findViewById(R.id.ll_multi).setOnClickListener(this);
        findViewById(R.id.ll_solar_system).setOnClickListener(this);
        findViewById(R.id.ll_progress).setOnClickListener(this);
        findViewById(R.id.ll_custom).setOnClickListener(this);
        findViewById(R.id.ll_full).setOnClickListener(this);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case 0:
                mCalendarView.setWeekStarWithSun();
                break;
            case 1:
                mCalendarView.setWeekStarWithMon();
                break;
            case 2:
                mCalendarView.setWeekStarWithSat();
                break;
            case 3:
                if (mCalendarView.isSingleSelectMode()) {
                    mCalendarView.setSelectDefaultMode();
                } else {
                    mCalendarView.setSelectSingleMode();
                }
                break;
            case 4:
                mCalendarView.setWeekView(MeizuWeekView.class);
                mCalendarView.setMonthView(MeiZuMonthView.class);
                mCalendarView.setWeekBar(EnglishWeekBar.class);
                break;
            case 5:
                mCalendarView.setAllMode();
                break;
            case 6:
                mCalendarView.setOnlyCurrentMode();
                break;
            case 7:
                mCalendarView.setFixMode();
                break;
        }
    }

    @Override
    public void onClick(View v) {
            if(v.getId()==R.id.ll_flyme) {
                MeiZuActivity.show(this);
                //CalendarActivity.show(this);
            }else if(v.getId()==R.id.ll_custom) {
                CustomActivity.show(this);
            } else if(v.getId()==R.id.ll_mix) {
                MixActivity.show(this);
            }else if(v.getId()==R.id.ll_full) {
                FullActivity.show(this);
            }else if(v.getId()==R.id.ll_range) {
                RangeActivity.show(this);
            }else if(v.getId()==R.id.ll_simple) {
                SimpleActivity.show(this);
            }else if(v.getId()==R.id.ll_colorful) {
                ColorfulActivity.show(this);
            }else if(v.getId()==R.id.ll_index) {
                IndexActivity.show(this);
            }else if(v.getId()==R.id.ll_tab) {
                ViewPagerActivity.show(this);
            }else if(v.getId()==R.id.ll_single) {
                SingleActivity.show(this);
            }else if(v.getId()==R.id.ll_multi) {
                MultiActivity.show(this);
            }else if(v.getId()==R.id.ll_solar_system) {
                SolarActivity.show(this);
            }else if(v.getId()==R.id.ll_progress) {
                ProgressActivity.show(this);
        }
    }

    private Calendar getSchemeCalendar(int year, int month, int day, int color, String text) {
        Calendar calendar = new Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        calendar.setSchemeColor(color);//如果单独标记颜色、则会使用这个颜色
        calendar.setScheme(text);
        return calendar;
    }


    @Override
    public void onCalendarOutOfRange(Calendar calendar) {
        Toast.makeText(this, String.format("%s : OutOfRange", calendar), Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onCalendarSelect(Calendar calendar, boolean isClick) {
        //Log.e("onDateSelected", "  -- " + calendar.getYear() + "  --  " + calendar.getMonth() + "  -- " + calendar.getDay());
        mTextLunar.setVisibility(View.VISIBLE);
        mTextYear.setVisibility(View.VISIBLE);
        mTextMonthDay.setText(calendar.getMonth() + "月" + calendar.getDay() + "日");
        mTextYear.setText(String.valueOf(calendar.getYear()));
        mTextLunar.setText(calendar.getLunar());
        mYear = calendar.getYear();
        if (isClick) {
            Toast.makeText(this, getCalendarText(calendar), Toast.LENGTH_SHORT).show();
        }
//        Log.e("lunar "," --  " + calendar.getLunarCalendar().toString() + "\n" +
//        "  --  " + calendar.getLunarCalendar().getYear());
        Log.e("onDateSelected", "  -- " + calendar.getYear() +
                "  --  " + calendar.getMonth() +
                "  -- " + calendar.getDay() +
                "  --  " + isClick + "  --   " + calendar.getScheme());
        Log.e("onDateSelected", "  " + mCalendarView.getSelectedCalendar().getScheme() +
                "  --  " + mCalendarView.getSelectedCalendar().isCurrentDay());
        Log.e("干支年纪 ： ", " -- " + TrunkBranchAnnals.getTrunkBranchYear(calendar.getLunarCalendar().getYear()));
    }

    @Override
    public void onCalendarLongClickOutOfRange(Calendar calendar) {
        Toast.makeText(this, String.format("%s : LongClickOutOfRange", calendar), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCalendarLongClick(Calendar calendar) {
        Toast.makeText(this, "长按不选择日期\n" + getCalendarText(calendar), Toast.LENGTH_SHORT).show();
    }

    private static String getCalendarText(Calendar calendar) {
        return String.format("新历%s \n 农历%s \n 公历节日：%s \n 农历节日：%s \n 节气：%s \n 是否闰月：%s",
                calendar.getMonth() + "月" + calendar.getDay() + "日",
                calendar.getLunarCalendar().getMonth() + "月" + calendar.getLunarCalendar().getDay() + "日",
                TextUtils.isEmpty(calendar.getGregorianFestival()) ? "无" : calendar.getGregorianFestival(),
                TextUtils.isEmpty(calendar.getTraditionFestival()) ? "无" : calendar.getTraditionFestival(),
                TextUtils.isEmpty(calendar.getSolarTerm()) ? "无" : calendar.getSolarTerm(),
                calendar.getLeapMonth() == 0 ? "否" : String.format("闰%s月", calendar.getLeapMonth()));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onMonthChange(int year, int month) {
        Log.e("onMonthChange", "  -- " + year + "  --  " + month);
        Calendar calendar = mCalendarView.getSelectedCalendar();
        mTextLunar.setVisibility(View.VISIBLE);
        mTextYear.setVisibility(View.VISIBLE);
        mTextMonthDay.setText(calendar.getMonth() + "月" + calendar.getDay() + "日");
        mTextYear.setText(String.valueOf(calendar.getYear()));
        mTextLunar.setText(calendar.getLunar());
        mYear = calendar.getYear();
    }

    @Override
    public void onViewChange(boolean isMonthView) {
        Log.e("onViewChange", "  ---  " + (isMonthView ? "月视图" : "周视图"));
    }


    @Override
    public void onWeekChange(List<Calendar> weekCalendars) {
        for (Calendar calendar : weekCalendars) {
            Log.e("onWeekChange", calendar.toString());
        }
    }

    @Override
    public void onYearViewChange(boolean isClose) {
        Log.e("onYearViewChange", "年视图 -- " + (isClose ? "关闭" : "打开"));
    }

    /**
     * 屏蔽某些不可点击的日期，可根据自己的业务自行修改
     *
     * @param calendar calendar
     * @return 是否屏蔽某些不可点击的日期，MonthView和WeekView有类似的API可调用
     */
    @Override
    public boolean onCalendarIntercept(Calendar calendar) {
        Log.e("onCalendarIntercept", calendar.toString());
        int day = calendar.getDay();
        return day == 1 || day == 3 || day == 6 || day == 11 || day == 12 || day == 15 || day == 20 || day == 26;
    }

    @Override
    public void onCalendarInterceptClick(Calendar calendar, boolean isClick) {
        Toast.makeText(this, calendar.toString() + "拦截不可点击", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onYearChange(int year) {
        mTextMonthDay.setText(String.valueOf(year));
        Log.e("onYearChange", " 年份变化 " + year);
    }

}


