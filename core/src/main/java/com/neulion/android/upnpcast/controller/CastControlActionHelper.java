package com.neulion.android.upnpcast.controller;

import android.os.Handler;
import android.os.Looper;

import com.neulion.android.upnpcast.controller.BaseCastEventSubscription.AvTransportSubscription;
import com.neulion.android.upnpcast.controller.BaseCastEventSubscription.RenderSubscription;
import com.neulion.android.upnpcast.util.CastUtils;
import com.neulion.android.upnpcast.util.ILogger;
import com.neulion.android.upnpcast.util.ILogger.DefaultLoggerImpl;

import org.fourthline.cling.controlpoint.SubscriptionCallback;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.support.avtransport.callback.GetMediaInfo;
import org.fourthline.cling.support.avtransport.callback.GetPositionInfo;
import org.fourthline.cling.support.avtransport.callback.Pause;
import org.fourthline.cling.support.avtransport.callback.Play;
import org.fourthline.cling.support.avtransport.callback.Seek;
import org.fourthline.cling.support.avtransport.callback.SetAVTransportURI;
import org.fourthline.cling.support.avtransport.callback.Stop;
import org.fourthline.cling.support.model.MediaInfo;
import org.fourthline.cling.support.model.PositionInfo;
import org.fourthline.cling.support.renderingcontrol.callback.GetMute;
import org.fourthline.cling.support.renderingcontrol.callback.GetVolume;
import org.fourthline.cling.support.renderingcontrol.callback.SetMute;
import org.fourthline.cling.support.renderingcontrol.callback.SetVolume;


/**
 * User: liuwei(wei.liu@neulion.com.com)
 * Date: 2018-07-03
 * Time: 15:18
 */
class CastControlActionHelper
{
    private ILogger mLogger = new DefaultLoggerImpl(CastControlActionHelper.class.getSimpleName());

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private ICastControlListener mControlListener;

    private Service mAVTransportService;

    private Service mRenderControlService;

    CastControlActionHelper(ICastControlListener listener)
    {
        mControlListener = listener;
    }

    public void setAVTransportService(Service castService)
    {
        mAVTransportService = castService;
    }

    public void setRenderControlService(Service renderControlService)
    {
        mRenderControlService = renderControlService;
    }

    public SetAVTransportURI setAvTransportAction(final String url, String metadata)
    {
        return new SetAVTransportURI(mAVTransportService, url, metadata)
        {
            @Override
            public void success(ActionInvocation invocation)
            {
                notifyCallback(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (mControlListener != null)
                        {
                            mControlListener.onOpen(url);
                        }
                    }
                });
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, final String defaultMsg)
            {
                mLogger.w(getClass().getSimpleName() + String.format(" [%s]", defaultMsg));

                notifyCallback(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (mControlListener != null)
                        {
                            mControlListener.onError(defaultMsg);
                        }
                    }
                });
            }
        };
    }

    public Play setPlayAction()
    {
        return new Play(mAVTransportService)
        {
            @Override
            public void success(ActionInvocation invocation)
            {
                notifyCallback(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (mControlListener != null)
                        {
                            mControlListener.onStart();
                        }
                    }
                });
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg)
            {
                mLogger.w(getClass().getSimpleName() + String.format(" [%s]", defaultMsg));
            }
        };
    }

    public Pause setPauseAction()
    {
        return new Pause(mAVTransportService)
        {
            @Override
            public void success(ActionInvocation invocation)
            {
                notifyCallback(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (mControlListener != null)
                        {
                            mControlListener.onPause();
                        }
                    }
                });
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg)
            {
                mLogger.w(getClass().getSimpleName() + String.format(" [%s]", defaultMsg));
            }
        };
    }

    public Stop setStopAction()
    {
        return new Stop(mAVTransportService)
        {
            @Override
            public void success(ActionInvocation invocation)
            {
                notifyCallback(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (mControlListener != null)
                        {
                            mControlListener.onStop();
                        }
                    }
                });
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg)
            {
                mLogger.w(getClass().getSimpleName() + String.format(" [%s]", defaultMsg));
            }
        };
    }

    public Seek setSeekAction(final int position)
    {
        return new Seek(mAVTransportService, CastUtils.getStringTime(position))
        {
            @Override
            public void success(ActionInvocation invocation)
            {
                notifyCallback(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        mControlListener.onSeekTo(position);
                    }
                });
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg)
            {
                mLogger.w(getClass().getSimpleName() + String.format(" [%s]", defaultMsg));
            }
        };
    }

    public SetVolume setVolumeAction(final long volume)
    {
        return new SetVolume(mRenderControlService, volume)
        {
            @Override
            public void success(ActionInvocation invocation)
            {
                notifyCallback(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (mControlListener != null)
                        {
                            mControlListener.onVolume(volume);
                        }
                    }
                });
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg)
            {
                mLogger.w(getClass().getSimpleName() + String.format(" [%s]", defaultMsg));
            }
        };
    }

    public SetMute setMuteAction(boolean mute)
    {
        return new SetMute(mRenderControlService, mute)
        {
            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg)
            {
                mLogger.w(getClass().getSimpleName() + String.format(" [%s]", defaultMsg));
            }
        };
    }

    public GetMute getMuteAction()
    {
        return new GetMute(mRenderControlService)
        {
            @Override
            public void received(ActionInvocation actionInvocation, final boolean currentMute)
            {
                notifyCallback(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (mControlListener != null && currentMute)
                        {
                            mControlListener.onVolume(0);
                        }
                    }
                });
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg)
            {
                mLogger.w(getClass().getSimpleName() + String.format(" [%s]", defaultMsg));
            }
        };
    }

    public GetVolume getVolumeAction()
    {
        return new GetVolume(mRenderControlService)
        {
            @Override
            public void received(ActionInvocation actionInvocation, final int currentVolume)
            {
                notifyCallback(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (mControlListener != null)
                        {
                            mControlListener.onVolume(currentVolume);
                        }
                    }
                });
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg)
            {
                mLogger.w(getClass().getSimpleName() + String.format(" [%s]", defaultMsg));
            }
        };
    }

    public GetMediaInfo getMediaInfo()
    {
        return new GetMediaInfo(mAVTransportService)
        {
            @Override
            public void received(ActionInvocation invocation, final MediaInfo mediaInfo)
            {
                if (mediaInfo != null)
                {
                    mLogger.i("getMediaInfo:" + mediaInfo.getCurrentURI() + "\nmetadata:" + mediaInfo.getCurrentURIMetaData() + "\nduration:" + mediaInfo
                            .getMediaDuration());
                }

                notifyCallback(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (mControlListener != null)
                        {
                            mControlListener.onSyncMediaInfo(null, mediaInfo);
                        }
                    }
                });
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg)
            {
                mLogger.w(getClass().getSimpleName() + String.format(" [%s]", defaultMsg));

                notifyCallback(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (mControlListener != null)
                        {
                            mControlListener.onSyncMediaInfo(null, null);
                        }
                    }
                });
            }
        };
    }

    public GetPositionInfo getPositionInfo()
    {
        return new GetPositionInfo(mAVTransportService)
        {
            @Override
            public void received(ActionInvocation invocation, final PositionInfo positionInfo)
            {
                mLogger.d("GetPositionInfo:" + positionInfo);

                notifyCallback(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (mControlListener != null)
                        {
                            mControlListener.onMediaPositionInfo(positionInfo);
                        }
                    }
                });
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg)
            {
                mLogger.w("seek failure:" + defaultMsg);
            }
        };
    }

    public SubscriptionCallback getAVTransportSubscription()
    {
        return new AvTransportSubscription(mAVTransportService, mControlListener);
    }

    public SubscriptionCallback getRenderSubscription()
    {
        return new RenderSubscription(mRenderControlService, mControlListener);
    }

    private void notifyCallback(Runnable runnable)
    {
        if (Thread.currentThread() != Looper.getMainLooper().getThread())
        {
            if (mHandler != null && mControlListener != null)
            {
                mHandler.post(runnable);
            }
        }
        else
        {
            runnable.run();
        }
    }

}
