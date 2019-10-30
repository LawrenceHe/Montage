package com.zhaodongdb.wireless;

import com.tmall.wireless.vaf.virtualview.event.EventData;
import com.tmall.wireless.vaf.virtualview.event.IEventProcessor;
import com.zhaodongdb.wireless.router.ZDRouter;

import static com.zhaodongdb.wireless.VirtualViewAction.ACTION_OPEN_NEW_PAGE;

public class VirtualViewEventProcessor implements IEventProcessor {

    @Override
    public boolean process(EventData data) {
        if (ACTION_OPEN_NEW_PAGE.equals(data.mVB.getAction())) {
            ZDRouter.navigation("zhaodong://native/montage/standard?pageName=" + data.mVB.getTag("pageName"));
        }
        return true;
    }
}
