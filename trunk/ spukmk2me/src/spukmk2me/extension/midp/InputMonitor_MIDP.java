/*
 *  SPUKMK2ME Engine - SPUKMK2 Engine for J2ME platform
 *  Copyright 2010 - 2011  HNYD Team
 *
 *  Original filename   : InputMonitor_MIDP.java
 *  Original package    : spukmk2me.video.midp
 *  Author              : Tuan Nguyen Quoc
 *  Note                :
 *  Classification      : MIDP extension
 *  Thread safety       : Undetermined
 *  Since               : 0.1
 *//*
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

package spukmk2me.extension.midp;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.game.GameCanvas;

import spukmk2me.input.IInputMonitor;

public abstract class InputMonitor_MIDP extends GameCanvas
    implements IInputMonitor
{
    protected InputMonitor_MIDP()
    {
        super( false );
        m_actionBitPattern  = IInputMonitor.ACT_NONE;
        m_pointerPosition   = 0xFFFFFFFF;
    }

    public final int GetActionBitPattern()
    {
        long currentTime = System.currentTimeMillis();

        m_keyCooldown  -= currentTime - m_keyLastTime;
        m_keyLastTime   = currentTime;

        if ( m_keyCooldown < 0 )
            m_keyCooldown = 0;

        if ( m_keyCooldown == 0 )
        {
            if ( m_actionBitPattern != 0 )
            {
                if ( m_keyCooldown < m_keyLatency )
                    m_keyCooldown = m_keyLatency;
            }

            return m_actionBitPattern;
        }
        else
            return ACT_NONE;
    }

    public final int GetTouchingPosition()
    {
        return m_pointerPosition;
    }

    public final void SetInputMode( byte inputMode )
    {
        m_inputMode = inputMode;

        if ( inputMode == INPUTMODE_NONE )
        {
            m_actionBitPattern  = ACT_NONE;
            m_pointerPosition   = 0xFFFFFFFF;
        }
    }

    public final void SetLatency( long latency )
    {
        m_keyCooldown   = m_keyLatency  = latency;
        m_keyLastTime   = System.currentTimeMillis();
    }

    public final void SetTemporaryLatency( long latency )
    {
        m_keyCooldown = latency;
    }

    protected void keyPressed( int keyCode )
    {
        if ( (m_inputMode & INPUTMODE_KEY) == 0 )
            return;

        switch ( keyCode )
        {
            case Canvas.KEY_NUM2:
                m_actionBitPattern |= ACT_UP;
                return;

            case Canvas.KEY_NUM4:
                m_actionBitPattern |= ACT_LEFT;
                return;

            case Canvas.KEY_NUM8:
                m_actionBitPattern |= ACT_DOWN;
                return;

            case Canvas.KEY_NUM6:
                m_actionBitPattern |= ACT_RIGHT;
                return;

            case Canvas.KEY_NUM5:
                m_actionBitPattern |= ACT_FIRE;
                return;
        }

        switch ( this.getGameAction( keyCode ) )
        {
            case Canvas.UP:
                m_actionBitPattern |= ACT_UP;
                return;

            case Canvas.LEFT:
                m_actionBitPattern |= ACT_LEFT;
                return;

            case Canvas.DOWN:
                m_actionBitPattern |= ACT_DOWN;
                return;

            case Canvas.RIGHT:
                m_actionBitPattern |= ACT_RIGHT;
                return;

            case Canvas.FIRE:
                m_actionBitPattern |= ACT_FIRE;
                return;
        }

        if ( keyCode == KeyCodeAdapter.SOFTKEY_RIGHT )
            m_actionBitPattern |= ACT_RSOFT;
    }

    protected void keyReleased( int keyCode )
    {
        if ( (m_inputMode & INPUTMODE_KEY) == 0 )
            return;

        switch ( keyCode )
        {
            case Canvas.KEY_NUM2:
                m_actionBitPattern &= ~ACT_UP;
                return;

            case Canvas.KEY_NUM4:
                m_actionBitPattern &= ~ACT_LEFT;
                return;

            case Canvas.KEY_NUM8:
                m_actionBitPattern &= ~ACT_DOWN;
                return;

            case Canvas.KEY_NUM6:
                m_actionBitPattern &= ~ACT_RIGHT;
                return;

            case Canvas.KEY_NUM5:
                m_actionBitPattern &= ~ACT_FIRE;
                return;
        }

        switch ( this.getGameAction( keyCode ) )
        {
            case Canvas.UP:
                m_actionBitPattern &= ~ACT_UP;
                return;

            case Canvas.LEFT:
                m_actionBitPattern &= ~ACT_LEFT;
                return;

            case Canvas.DOWN:
                m_actionBitPattern &= ~ACT_DOWN;
                return;

            case Canvas.RIGHT:
                m_actionBitPattern &= ~ACT_RIGHT;
                return;

            case Canvas.FIRE:
                m_actionBitPattern &= ~ACT_FIRE;
                return;
        }

        if ( keyCode == KeyCodeAdapter.SOFTKEY_RIGHT )
            m_actionBitPattern &= ~ACT_RSOFT;
    }

    protected void pointerPressed( int x, int y )
    {
        if ( (m_inputMode & INPUTMODE_TOUCH) == 0 )
            return;

        m_actionBitPattern |= IInputMonitor.ACT_FIRE;
        m_pointerPosition   = ((short)x << 16) | (short)y;
    }

    protected void pointerReleased( int x, int y )
    {
        if ( (m_inputMode & INPUTMODE_TOUCH) == 0 )
            return;

        m_actionBitPattern &= ~IInputMonitor.ACT_FIRE;
        m_pointerPosition   = 0xFFFFFFFF;
    }

    private long    m_keyCooldown, m_keyLatency, m_keyLastTime;
    private int     m_actionBitPattern;
    private int     m_pointerPosition;
    private byte    m_inputMode;
}
