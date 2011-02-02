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
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.spukmk2me.gameflow;

import javax.microedition.midlet.MIDlet;

import com.spukmk2me.Util;

public abstract class GameMIDlet extends MIDlet
{
    protected GameMIDlet( IGameProcessor gameProcessor )
    {
        m_gameProcessor = gameProcessor;
    }
    
    protected final void startApp()
    {
        if ( m_gameThread == null )
        {
            Util.InitialiseRandomSeed( System.currentTimeMillis() );
            m_gameThread =
                new GameThread( this, m_gameProcessor, new ConfigData() );
        }
        
        m_gameThread.Resume();
    }
    
    protected final void pauseApp()
    {
        m_gameThread.Pause();
    }
    
    protected final void destroyApp( boolean forceToQuit )
    {
        QuitApplication();
    }
    
    public final void QuitApplication()
    {
        m_gameThread.Quit();
        m_gameThread = null;
        Runtime.getRuntime().gc();
        this.notifyDestroyed();
    }

    protected   GameThread      m_gameThread = null;
    private     IGameProcessor  m_gameProcessor;
}
