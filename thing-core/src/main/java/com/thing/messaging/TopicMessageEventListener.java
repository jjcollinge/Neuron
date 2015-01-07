package com.thing.messaging;

import com.thing.api.events.MessageEvent;
import com.thing.api.events.MessageEventListener;

public interface TopicMessageEventListener extends MessageEventListener {

	public void onMessageArrived(MessageEvent event, String topic);
		
}
