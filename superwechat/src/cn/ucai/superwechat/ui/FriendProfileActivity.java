package cn.ucai.superwechat.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.easeui.domain.User;
import com.hyphenate.easeui.utils.EaseUserUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.superwechat.I;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SuperWeChatHelper;
import cn.ucai.superwechat.domain.Result;
import cn.ucai.superwechat.net.NetDao;
import cn.ucai.superwechat.net.OnCompleteListener;
import cn.ucai.superwechat.utils.L;
import cn.ucai.superwechat.utils.MFGT;
import cn.ucai.superwechat.utils.ResultUtils;

public class FriendProfileActivity extends BaseActivity {

    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.txt_title)
    TextView txtTitle;
    @BindView(R.id.ivAvatar)
    ImageView ivAvatar;
    @BindView(R.id.tvNickName)
    TextView tvNickName;
    @BindView(R.id.tvUsername)
    TextView tvUsername;
    @BindView(R.id.rl_tag)
    RelativeLayout rlTag;
    @BindView(R.id.addContact)
    Button addContact;
    @BindView(R.id.btn_sendMessage)
    Button btnSendMessage;
    @BindView(R.id.btn_video_call)
    Button btnVideoCall;
    @BindView(R.id.layout_btn)
    LinearLayout layoutBtn;

    private static final String TAG = FriendProfileActivity.class.getSimpleName();
    User user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        imgBack.setVisibility(View.VISIBLE);
        txtTitle.setVisibility(View.VISIBLE);
        txtTitle.setText(R.string.userinfo_txt_profile);
        user = (User) getIntent().getSerializableExtra(I.User.TABLE_NAME);
        L.e(TAG, ">>>>>>>>>>>USER=" + user);
        if (user != null) {
            showUserInfo();
        } else {
            String username = getIntent().getStringExtra(I.User.USER_NAME);
            if (username == null) {
                MFGT.finishActivity(this);
            } else {
                syncUserInfo(username);
            }
        }

    }

    private void syncUserInfo(String username) {
        NetDao.getUserInfoByUsername(this, username, new OnCompleteListener<String>() {
            @Override
            public void onSuccess(String s) {
                if (s != null) {
                    Result result = ResultUtils.getResultFromJson(s, User.class);
                    if (result != null) {
                        if (result.isRetMsg()) {
                            User u = (User) result.getRetData();
                            if (u != null) {
                                user = u;
                                showUserInfo();
                            }
                        }
                    }
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    private void showUserInfo() {
        tvUsername.setText(user.getMUserName());
        tvNickName.setText(user.getMUserNick());
        EaseUserUtils.setAppUserAvatarByPath(this, user.getAvatar(), ivAvatar);
        if (isFriend()) {
            btnSendMessage.setVisibility(View.VISIBLE);
            btnVideoCall.setVisibility(View.VISIBLE);
        } else {
            addContact.setVisibility(View.VISIBLE);
        }
    }

    public boolean isFriend() {
        User u = SuperWeChatHelper.getInstance().getAppContactList().get(user.getMUserName());
        if (u == null) {
            return false;
        } else {
            SuperWeChatHelper.getInstance().saveAppContact(user);
            return true;
        }
    }

    @OnClick({R.id.rl_tag, R.id.addContact, R.id.btn_sendMessage, R.id.btn_video_call, R.id.img_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_tag:
                break;
            case R.id.addContact:
                MFGT.gotoAddFriend(this, user.getMUserName());
                break;
            case R.id.btn_sendMessage:
                MFGT.gotoChat(this, user.getMUserName());
                break;
            case R.id.btn_video_call:
                startActivity(new Intent(this, VideoCallActivity.class).putExtra("username", user.getMUserName())
                        .putExtra("isComingCall", false));
                break;
            case R.id.img_back:
                MFGT.finishActivity(this);
                break;
        }
    }
}
