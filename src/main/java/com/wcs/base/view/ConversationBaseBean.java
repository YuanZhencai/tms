package com.wcs.base.view;

import javax.enterprise.context.Conversation;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.wcs.base.model.BaseEntity;

public abstract class ConversationBaseBean<T extends BaseEntity> extends BaseBean<T> implements IBaseBean {
    private static final long serialVersionUID = 1L;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Inject
    private Conversation conversation;

    public void initConversation() {
        if (conversation.isTransient()) {
            conversation.begin();
        }
    }

    public void endConversation() {
        if (!conversation.isTransient()) {
            conversation.end();
        }
    }

    public String save() {
        super.saveEntity();

        this.endConversation();
        return listPage == null ? "/faces/debug/failed.xhtml" : listPage;
    }

    public String cancel() {
        this.endConversation();
        return "cancelled";
    }

    public String delete() {
        super.deleteEntity();
        this.endConversation();
        return listPage == null ? "/faces/debug/failed.xhtml" : listPage;
    }

}
