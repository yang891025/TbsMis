package com.tbs.tbsmis.Exam.utils;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by TBS on 2017/11/30.
 */

public class DataUtils
{

    //服务器端
    public static String HOST = "http://e.tbs.com.cn:1115/";

    //考试详情接口
    public static final String URL_EXAM_INFO = HOST + "servlet/mobileApi/getExamInfo.cbs";


    public static List<com.tbs.tbsmis.Exam.bean.AnswerInfo> createAllExamDatas(JSONArray answerArray,JSONArray optionsArray) {

        List<com.tbs.tbsmis.Exam.bean.AnswerInfo> examDatas = new ArrayList<com.tbs.tbsmis.Exam.bean.AnswerInfo>();
        for (int i = 0; i < answerArray.length(); i++) {
            com.tbs.tbsmis.Exam.bean.AnswerInfo item = new com.tbs.tbsmis.Exam.bean.AnswerInfo();
            try {
                JSONObject json = answerArray.getJSONObject(i);
                // 试题数据
                String s_id = json.getString("s_id");
                String s_content = json.getString("s_content");
                String s_type = json.getString("s_type");
                String s_answer = json.getString("s_answer");
                String score = json.getString("score");
                String s_description = json.getString("s_description");
                String s_source = json.getString("s_source");
                String s_identification = json.getString("s_identification");
                item.setQuestionId(s_id);// 试题主键
                item.setQuestionName(s_content);// 试题题目
                item.setQuestionType(s_type);// 试题类型0单选1多选
                item.setQuestionFor("0");// （0模拟试题，1竞赛试题）
                item.setAnalysis(s_description);// 试题分析
                item.setCorrectAnswer(s_answer);// 正确答案
                item.setScore(score);// 分值
                item.setOption_type("0");
               if(s_type.equalsIgnoreCase("panduan")){
                   item.setOptionA("正确");
                   item.setOptionB("错误");
               }else{
                   for (int j = 0; j < optionsArray.length(); j++) {
                       JSONObject ojson = optionsArray.getJSONObject(j);
                       String os_id = ojson.getString("s_id");
                       String o_content = ojson.getString("o_content");
                       String o_ord = ojson.getString("o_ord");
//                       System.out.println("s_id = "+ s_id +"&os_id = "+ os_id +"&o_ord = "+ o_ord+ "&o_content = "+o_content);
                       if(os_id.equalsIgnoreCase(s_id)){
                           if(o_ord.equals("1")){
                               item.setOptionA(o_content);
                           }else if(o_ord.equals("2")){
                               item.setOptionB(o_content);
                           }else if(o_ord.equals("3")){
                               item.setOptionC(o_content);
                           }else if(o_ord.equals("4")){
                               item.setOptionD(o_content);
                           }else if(o_ord.equals("5")){
                               item.setOptionE(o_content);
                           }
                       }
                   }

               }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            examDatas.add(item);
        }
     return examDatas;
    }

    public static void setSharePerference(Context context,
                                          String perferenceName, String MapName, String value) {
        SharedPreferences setting = context.getSharedPreferences(
                perferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = setting.edit();
        editor.putString(MapName, value);
        editor.commit();
    }


    public static String getShareperference(Context context,
                                            String perferenceName, String MapName, String defvalue) {
        String MsgNum = null;
        SharedPreferences Getting = context.getSharedPreferences(
                perferenceName, Context.MODE_PRIVATE);
        MsgNum = Getting.getString(MapName, defvalue);
        return MsgNum;
    }

    public static String getStringTime(long time) {
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String startTime = sdf.format(date);
        return startTime;
    }

}
