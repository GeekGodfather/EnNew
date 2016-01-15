package com.ennew.ui.act;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.ennew.R;
import com.ennew.app.AppManager;
import com.ennew.db.DataBaseManager;
import com.ennew.model.Conversation;
import com.ennew.model.MessageInfo;
import com.ennew.ui.adapter.ConversationAdapter;

import java.util.List;

/**
 * 会话列表页面
 *
 * @author jianglihui
 */
public class ConversationActivity extends Activity implements OnClickListener,
        OnItemClickListener {

    private AppManager appManager;
    private DataBaseManager dbManager;
    private ListView conversation_lv;
    ConversationAdapter convAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_record);
        setTitle();
        if (appManager == null) {
            appManager = AppManager.getAppManager();
            appManager.addActivity(this);
        }
        if (dbManager == null) {
            dbManager = DataBaseManager.getInstance();
        }
        initView();

    }

    private void setTitle() {
        ImageView title_left = (ImageView) findViewById(R.id.title_left);
        title_left.setOnClickListener(this);

    }

    private void initView() {
        conversation_lv = (ListView) findViewById(R.id.conversation_lv);
        convAdapter = new ConversationAdapter(ConversationActivity.this);
        conversation_lv.setAdapter(convAdapter);
        conversation_lv.setOnItemClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        queryConversations();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void queryConversations() {
        QueryConversationsAsyncTask conversationsAsyn = new QueryConversationsAsyncTask();
        conversationsAsyn.execute();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_left:
                finish();
                break;

        }
    }

    private class QueryConversationsAsyncTask extends
            AsyncTask<Void, ProgressDialog, List<Conversation>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(List<Conversation> result) {
            if (result != null && !result.isEmpty()) {
                if (convAdapter != null) {
                    convAdapter.setNewList(result);
                }
            }
        }

        @Override
        protected List<Conversation> doInBackground(Void... params) {
            List<Conversation> queryConverList = dbManager.queryConverList();
            for (Conversation conversation : queryConverList) {
                String fromuserid = conversation.getFromuserid();
                String touserid = conversation.getTouserid();
                MessageInfo info = dbManager
                        .queryLastMessageInfoByFromIdAndToId(fromuserid,
                                touserid);
                if (info != null) {
                    conversation.setLastMeaage(info.getContent());
                    conversation.setContentType(info.getContentType());
                    conversation.setLastTime(info.getTimesTamp());
                }
            }
            return queryConverList;
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        Conversation conversation = convAdapter.getItem(position);
        String touserid = conversation.getTouserid();
        Intent intent = new Intent();
        intent.setClass(ConversationActivity.this, ChatActivity.class);
        intent.putExtra(ChatActivity.INTENT_TOUSERID, touserid);
        startActivity(intent);
    }

}
