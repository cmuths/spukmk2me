/*
 *  SPUKMK2me - SPUKMK2 Engine for J2ME platform
 *  Copyright 2010 - 2011  HNYD Team
 *
 *   SPUKMK2me is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *   SPUKMK2me is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *  along with SPUKMK2me.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.spukmk2me.gameflow;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;

import com.spukmk2me.Device;
import com.spukmk2me.video.IVideoDriver;
import com.spukmk2me.sound.ISoundMonitor;
import com.spukmk2me.scene.SceneManager;
import com.spukmk2me.input.IInputMonitor;

public final class GameThread extends Thread
{
    public GameThread( GameMIDlet owner, IGameProcessor gameProcessor,
        ConfigData configData )
    {
        m_owner                 = owner;
        m_gameProcessor         = gameProcessor;
        m_configData            = configData;
        m_firstActivatedTime    = true;
        m_alive                 = true;
        m_working               = false;
    }

    public void Resume()
    {
        if ( m_firstActivatedTime )
        {
            m_device    = m_gameProcessor.Init( m_configData );
            m_vdriver   = m_device.GetVideoDriver();
            m_smonitor  = m_device.GetSoundMonitor();
            m_scene     = m_device.GetSceneManager();
            m_imonitor  = m_device.GetInputMonitor();
            this.start();
            m_firstActivatedTime = false;
        }

        Display.getDisplay( m_owner ).setCurrent(
            (Displayable)m_vdriver.GetMIDPDisplayable() );
        m_working = true;
    }

    public void Pause()
    {
        m_working = false;
    }

    public void Quit()
    {
        m_alive = false;
    }

    public void run()
    {
        long waitTime, lastWakeupTime, lastProcessTime, timeToProcess;

        m_vdriver.PrepareRenderingContext();
        m_vdriver.StartInternalClock();
        m_imonitor.SetInputMode( IInputMonitor.INPUTMODE_KEY );
        lastWakeupTime = lastProcessTime = System.currentTimeMillis();

        while ( m_alive )
        {
            if ( m_working )
            {
                //synchronized( this )
                {
                    waitTime = System.currentTimeMillis() - lastProcessTime;
                    lastProcessTime += waitTime;
                    timeToProcess =
                        ( waitTime > m_configData.MAX_MS_PER_FRAME )?
                        m_configData.MAX_MS_PER_FRAME : waitTime;
                    
                    m_configData.INPUT_ACTION =
                        m_imonitor.GetActionBitPattern();

                    m_gameProcessor.ProcessGame( timeToProcess );

                    if ( m_configData.AUTO_VIDEO_OUTPUT )
                    {
                        m_vdriver.StartRendering( false, 0x00000000,
                            timeToProcess );
                        m_scene.RenderAll();
                        m_vdriver.FinishRendering();
                    }
                }

                waitTime = lastWakeupTime + m_configData.MS_PER_FRAME -
                    System.currentTimeMillis();

                if ( waitTime > 0 )
                {
                    try
                    {
                        Thread.sleep( waitTime );
                    } catch ( InterruptedException e ) {
                        //#ifdef __SPUKMK2ME_DEBUG
                        e.printStackTrace();
                        //#endif
                    }
                }
                else
                    Thread.yield();

                lastWakeupTime = System.currentTimeMillis();
            }
            else
            {
                try
                {
                    Thread.sleep( m_configData.MS_PER_FRAME );
                } catch ( InterruptedException e )
                {
                    //#ifdef __SPUKMK2ME_DEBUG
                    e.printStackTrace();
                    //#endif
                }

                lastProcessTime = lastWakeupTime = System.currentTimeMillis();
            }

            if ( m_gameProcessor.IsFinished() )
                m_alive = false;
        }

        m_vdriver.CleanupRenderingContext();
        m_device.StopDevice();
        m_owner.QuitApplication();
    }

    private Device          m_device;
    private IVideoDriver    m_vdriver;
    private ISoundMonitor   m_smonitor;
    private IInputMonitor   m_imonitor;
    private SceneManager    m_scene;

    private GameMIDlet      m_owner;
    private IGameProcessor  m_gameProcessor;
    private ConfigData      m_configData;

    private boolean m_alive, m_working, m_firstActivatedTime;
}
