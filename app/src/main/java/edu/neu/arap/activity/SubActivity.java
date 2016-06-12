package edu.neu.arap.activity;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import edu.neu.arap.R;

public class SubActivity extends ListActivity {

    /////////////
    String[] from={"name","id"};              //这里是ListView显示内容每一列的列名
    int[] to={R.id.user_name, R.id.user_id};   //这里是ListView显示每一列对应的list_item中控件的id

    String[] userName={"zhangsan","lisi","wangwu","zhaoliu"}; //这里第一列所要显示的人名
    String[] userId={"1001","1002","1003","1004"};  //这里是人名对应的ID

    ArrayList<HashMap<String,String>> list=null;
    HashMap<String,String> map=null;
    //////////////////
    String type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        Intent intent=getIntent();
        int id=intent.getIntExtra("type",0);
        switch (id)
        {
            case R.id.movie:
                type="电影";
                break;
            case R.id.travel:
                type="旅游";
                break;
            case R.id.leisure:
                type="娱乐";
                break;
            case R.id.restaurant:
                type="美食";
                break;
                default:
                    type="视镜";
                    break;
        }
        EditText editText=(EditText) findViewById(R.id.sub_editText);
        editText.setText(type);
        findViewById(R.id.sub_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SubActivity.this,MainActivityNew.class));
                finish();
            }
        });
        ///////////////////
        list=new ArrayList<HashMap<String,String>>();
        for(int i=0; i<4; i++){
            map=new HashMap<String,String>();
            map.put("id", userId[i]);
            map.put("name", userName[i]);
            list.add(map);
        }
        SimpleAdapter adapter=new SimpleAdapter(this,list,R.layout.list_item,from,to);
        setListAdapter(adapter);
        /////////////////
    }
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Toast.makeText(v.getContext(),""+id,Toast.LENGTH_SHORT).show();
    }
}
