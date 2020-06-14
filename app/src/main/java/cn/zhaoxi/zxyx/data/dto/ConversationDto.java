package cn.zhaoxi.zxyx.data.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class ConversationDto implements Serializable {
        private int unReadMsgCnt;
        private LetterDto latestMessage;
}
