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

package com.spukmk2me.extension.midp;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.game.GameCanvas;

/* $if SPUKMK2ME_DEBUG$ */
import com.spukmk2me.debug.Logger;
/* $endif$ */
import com.spukmk2me.input.IInputMonitor;

public abstract class InputMonitor_MIDP extends GameCanvas
    implements IInputMonitor
{
    protected InputMonitor_MIDP()
    {
        super( false );
        DetectExternalKeyCodes();
        m_actionBitPattern      = IInputMonitor.ACT_NONE;
        m_pointerPosition       = 0xFFFFFFFF;
        m_behaviourList         = new byte[ 32 ];
        m_touchSKNotExecuted    = true;
    }

    public final int GetInputStates()
    {
        long currentTime = System.currentTimeMillis();

        m_keyCooldown  -= currentTime - m_keyLastTime;
        m_keyLastTime   = currentTime;

        if ( m_keyCooldown < 0 )
            m_keyCooldown = 0;

        if ( m_keyCooldown == 0 )
        {
            PostTouchProcess();

            if ( m_actionBitPattern != 0 )
            {
                if ( m_keyCooldown < m_keyLatency )
                    m_keyCooldown = m_keyLatency;
            }

            m_producedActions = m_actionBitPattern;

            RescanInputAction();
        }
        else
            m_producedActions = 0;
        
        return m_producedActions;
    }
    
    public final boolean Acted( int inputAction )
    {
        return (m_producedActions & inputAction) != 0;
    }

    public final int GetTouchingPosition()
    {
        return m_pointerPosition;
    }

    public final void SetInputMode( int inputMode )
    {
        m_inputMode = inputMode;

        if ( inputMode == INPUTMODE_NONE )
        {
            m_actionBitPattern  = ACT_NONE;
            m_pointerPosition   = 0xFFFFFFFF;
        }
    }

    public final int GetInputCapability()
    {
        int inputCapability = INPUTMODE_KEY;

        if ( this.hasPointerEvents() )
            inputCapability |= INPUTMODE_TOUCH;

        return inputCapability;
    }

    public final void SetInputBehaviour( int action, byte behaviour )
    {
        /* $if SPUKMK2ME_DEBUG$ */
        if ( action == 0 )
        {
            Logger.Trace( "Inputed action is 0. No change is applied" );
            return;
        }
        /* $endif$ */

        int index = 0;

        while ( (action & 0x00000001) == 0 )
        {
            ++index;
            action >>= 1;
        }

        m_behaviourList[ index ] = behaviour;
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
            case Canvas.KEY_NUM0:
                m_actionBitPattern |= ACT_NUM0;
                return;

            case Canvas.KEY_NUM1:
                m_actionBitPattern |= ACT_NUM1;
                return;
            
            case Canvas.KEY_NUM2:
                m_actionBitPattern |= ACT_UP | ACT_NUM2;
                return;

            case Canvas.KEY_NUM3:
                m_actionBitPattern |= ACT_NUM3;
                return;

            case Canvas.KEY_NUM4:
                m_actionBitPattern |= ACT_LEFT | ACT_NUM4;
                return;

            case Canvas.KEY_NUM5:
                m_actionBitPattern |= ACT_FIRE | ACT_NUM5;
                return;

            case Canvas.KEY_NUM6:
                m_actionBitPattern |= ACT_RIGHT | ACT_NUM6;
                return;

            case Canvas.KEY_NUM7:
                m_actionBitPattern |= ACT_NUM7;
                return;

            case Canvas.KEY_NUM8:
                m_actionBitPattern |= ACT_DOWN | ACT_NUM8;
                return;

            case Canvas.KEY_NUM9:
                m_actionBitPattern |= ACT_NUM9;
                return;

            case Canvas.KEY_POUND:
                m_actionBitPattern |= ACT_NUMPND;
                return;

            case Canvas.KEY_STAR:
                m_actionBitPattern |= ACT_NUMSTAR;
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

        if ( keyCode == m_softleftKeyCode )
            m_actionBitPattern |= ACT_LSOFT;
        else if( keyCode == m_softrightKeyCode )
            m_actionBitPattern |= ACT_RSOFT;
    }

    protected void keyReleased( int keyCode )
    {
        if ( (m_inputMode & INPUTMODE_KEY) == 0 )
            return;

        switch ( keyCode )
        {
            case Canvas.KEY_NUM0:
                ApplyKeyReleasing( ACT_NUM0 );
                return;

            case Canvas.KEY_NUM1:
                ApplyKeyReleasing( ACT_NUM1 );
                return;

            case Canvas.KEY_NUM2:
                ApplyKeyReleasing( ACT_UP );
                ApplyKeyReleasing( ACT_NUM2 );
                return;

            case Canvas.KEY_NUM3:
                ApplyKeyReleasing( ACT_NUM3 );
                return;

            case Canvas.KEY_NUM4:
                ApplyKeyReleasing( ACT_LEFT );
                ApplyKeyReleasing( ACT_NUM4 );
                return;

            case Canvas.KEY_NUM5:
                ApplyKeyReleasing( ACT_FIRE );
                ApplyKeyReleasing( ACT_NUM5 );
                return;

            case Canvas.KEY_NUM6:
                ApplyKeyReleasing( ACT_RIGHT );
                ApplyKeyReleasing( ACT_NUM6 );
                return;

            case Canvas.KEY_NUM7:
                ApplyKeyReleasing( ACT_NUM7 );
                return;

            case Canvas.KEY_NUM8:
                ApplyKeyReleasing( ACT_DOWN );
                ApplyKeyReleasing( ACT_NUM8 );
                return;

            case Canvas.KEY_NUM9:
                ApplyKeyReleasing( ACT_NUM9 );
                return;

            case Canvas.KEY_POUND:
                ApplyKeyReleasing( ACT_NUMPND );
                return;

            case Canvas.KEY_STAR:
                ApplyKeyReleasing( ACT_NUMSTAR );
                return;
        }

        switch ( this.getGameAction( keyCode ) )
        {
            case Canvas.UP:
                ApplyKeyReleasing( ACT_UP );
                return;

            case Canvas.LEFT:
                ApplyKeyReleasing( ACT_LEFT );
                return;

            case Canvas.DOWN:
                ApplyKeyReleasing( ACT_DOWN );
                return;

            case Canvas.RIGHT:
                ApplyKeyReleasing( ACT_RIGHT );
                return;

            case Canvas.FIRE:
                ApplyKeyReleasing( ACT_FIRE );
                return;
        }

        if ( keyCode == m_softleftKeyCode )
            ApplyKeyReleasing( ACT_LSOFT );
        else if( keyCode == m_softrightKeyCode )
            ApplyKeyReleasing( ACT_RSOFT );
    }

    protected void pointerPressed( int x, int y )
    {
        if ( (m_inputMode & INPUTMODE_TOUCH) == 0 )
            return;

        m_pointerPosition   = ((short)x << 16) | (short)y;
        m_pressedX          = (short)x;
        m_pressedY          = (short)y;

        if ( (m_inputMode & INPUTMODE_TOUCH_SK) != 0 )
        {
            m_touchHoldLastTime = System.currentTimeMillis();
            m_touchHolding      = true;
        }

        if ( (m_inputMode & INPUTMODE_TOUCH_SK_FIX_NOKIAS40) != 0 )
        {
            if ( y >= this.getHeight() - 48 )
            {
                int buttonSize = this.getWidth() / 3;

                if ( x <= buttonSize )
                {
                    m_actionBitPattern |= ACT_LSOFT;
                    ApplyKeyReleasing( ACT_LSOFT );
                }
                else if ( x <= (buttonSize << 1) )
                {
                    m_actionBitPattern |= ACT_FIRE;
                    ApplyKeyReleasing( ACT_FIRE );
                }
                else
                {
                    m_actionBitPattern |= ACT_RSOFT;
                    ApplyKeyReleasing( ACT_RSOFT );
                }
            }
        }
    }

    protected void pointerDragged( int x, int y )
    {
        if ( (m_inputMode & INPUTMODE_TOUCH) == 0 )
            return;

        m_pointerPosition = ((short)x << 16) | (short)y;

        if ( (m_inputMode & INPUTMODE_TOUCH_SK) != 0 )
        {
            m_touchHolding = false;

            if ( m_touchSKNotExecuted )
            {
                if ( x - m_pressedX > 20 )
                {
                    m_actionBitPattern         |= ACT_RIGHT;
                    m_touchSimulatedActions    |= ACT_RIGHT;
                    m_touchSKNotExecuted        = false;
                }
                else if ( x - m_pressedX < -20 )
                {
                    m_actionBitPattern         |= ACT_LEFT;
                    m_touchSimulatedActions    |= ACT_LEFT;
                    m_touchSKNotExecuted        = false;
                }

                if ( y - m_pressedY > 20 )
                {
                    m_actionBitPattern         |= ACT_DOWN;
                    m_touchSimulatedActions    |= ACT_DOWN;
                    m_touchSKNotExecuted        = false;
                }
                else if ( y - m_pressedY < -20 )
                {
                    m_actionBitPattern         |= ACT_UP;
                    m_touchSimulatedActions    |= ACT_UP;
                    m_touchSKNotExecuted        = false;
                }
            }
        }
    }

    protected void pointerReleased( int x, int y )
    {
        if ( (m_inputMode & INPUTMODE_TOUCH) == 0 )
            return;

        m_pointerPosition = 0xFFFFFFFF;
        
        if ( (m_inputMode & INPUTMODE_TOUCH_SK) != 0 )
        {
            m_touchHolding = false;

            int action = 1;

            for ( int i = 32; i != 0; --i )
            {
                if ( (m_touchSimulatedActions & 0x00000001) != 0 )
                    ApplyKeyReleasing( action );

                action <<= 1;
                m_touchSimulatedActions >>= 1;
            }
            
            m_touchSimulatedActions = 0;
            m_touchSKNotExecuted    = true;
        }
    }

    private void ApplyKeyReleasing( int action )
    {
        int index = 0;
        
        while ( (action & 0x00000001) == 0 )
        {
            ++index;
            action >>= 1;
        }

        action  &= 0x00000001;
        action <<= index;

        if ( m_behaviourList[ index ] != BHV_FIX_DOUBLE_SIGNAL_PR )
            m_actionBitPattern &= ~action;
    }

    private void PostTouchProcess()
    {
        if ( (m_inputMode & INPUTMODE_TOUCH_SK) != 0 )
        {
            if ( m_touchSKNotExecuted && m_touchHolding )
            {
                if ( System.currentTimeMillis() - m_touchHoldLastTime > 750 )
                {
                    m_actionBitPattern |= ACT_FIRE;
                    m_touchSimulatedActions |= ACT_FIRE;
                    m_touchSKNotExecuted = false;
                }
            }
        }
    }

    private void RescanInputAction()
    {
        if ( m_actionBitPattern == 0 )
            return;

        int index   = 0;
        int action  = m_actionBitPattern;
        int mask    = 0x00000001;

        while ( index != 32 )
        {
            if ( (action & 0x00000001) != 0 )
            {
                if ( m_behaviourList[ index ] != BHV_ENABLE_WHEN_HOLDING )
                    m_actionBitPattern &= ~mask;
            }

            ++index;
            mask      <<= 1;
            action    >>= 1;
        }
    }

    // This function detect key codes for keys which aren't standardised in
    // MIDP specification.
    // I wrote this function based on some information on the Internet. So I
    // can't guarantee that it can function properly.
    private void DetectExternalKeyCodes()
    {
        DetectPlatform();
        DetectSoftLeftKeyCode();
        DetectSoftRightKeyCode();
    }

    private void DetectPlatform()
    {
        String platformHint =
            System.getProperty( "microedition.platform" );

        ///////////////////////////////
        // Nokia detection ("Nokia")
        if ( platformHint.toUpperCase().indexOf( "NOKIA" ) != -1 ) // Nokia
        {
            m_platform = PLATFORM_NOKIA;
            return;
        }

        try
        {
            Class.forName( "com.nokia.mid.ui.FullCanvas" );
            m_platform = PLATFORM_NOKIA;
            return;
        } catch ( ClassNotFoundException e ) {}

        
        ///////////////////////////////
        // Sony Ericsson detection ("SonyEricsson")
        if ( platformHint.toUpperCase().indexOf( "SONYERICSSON" ) != -1 )
        {
            m_platform = PLATFORM_SE;
            return;
        }

        
        ///////////////////////////////
        // LG detection
        try
        {
            Class.forName( "mmpp.media.MediaPlayer" );
            m_platform = PLATFORM_LG;
            return;
        } catch ( ClassNotFoundException e ) {}

        try
        {
            Class.forName( "mmpp.phone.Phone" );
            m_platform = PLATFORM_LG;
            return;
        } catch ( ClassNotFoundException e ) {}

        try
        {
            Class.forName( "mmpp.lang.MathFP" );
            m_platform = PLATFORM_LG;
            return;
        } catch ( ClassNotFoundException e ) {}

        try
        {
            Class.forName( "mmpp.media.BackLight" );
            m_platform = PLATFORM_LG;
            return;
        } catch ( ClassNotFoundException e ) {}

        
        ///////////////////////////////
        // Motorola detection
        try
        {
            Class.forName( "com.motorola.multimedia.Vibrator" );
            m_platform = PLATFORM_MOTOROLA;
            return;
        } catch ( ClassNotFoundException e ) {}

        try
        {
            Class.forName( "com.motorola.multimedia.Lighting" );
            m_platform = PLATFORM_MOTOROLA;
            return;
        } catch ( ClassNotFoundException e ) {}

        try
        {
            Class.forName( "com.motorola.multimedia.FunLight" );
            m_platform = PLATFORM_MOTOROLA;
            return;
        } catch ( ClassNotFoundException e ) {}

        try
        {
            Class.forName( "com.motorola.graphics.j3d.Effect3D" );
            m_platform = PLATFORM_MOTOROLA;
            return;
        } catch ( ClassNotFoundException e ) {}

        
        ///////////////////////////////
        // Samsung detection
        try
        {
            Class.forName( "com.samsung.util.Vibration" );
            m_platform = PLATFORM_SAMSUNG;
            return;
        } catch ( ClassNotFoundException e ) {}

        
        ///////////////////////////////
        // Siemens detection
        try
        {
            Class.forName( "com.siemens.mp.io.File" );
            m_platform = PLATFORM_SIEMENS;
            return;
        } catch ( ClassNotFoundException e ) {}

        m_platform = PLATFORM_UNKNOWN;
    }

    private void DetectSoftLeftKeyCode()
    {
        if ( m_platform != PLATFORM_UNKNOWN )
        {
            String keyWord = "SOFT";
            int i;

            for ( i = 0; i != SOFTLEFT_KEYCODES[ m_platform ].length; ++i )
            {
                try
                {
                    if ( this.getKeyName(
                        SOFTLEFT_KEYCODES[ m_platform ][ i ] ).toUpperCase().
                        indexOf( keyWord ) != -1 )
                    {
                        m_softleftKeyCode =
                            SOFTLEFT_KEYCODES[ m_platform ][ i ];
                        return;
                    }
                } catch ( IllegalArgumentException e ) {}
            }
        }

        m_softleftKeyCode = KEY_DEFAULT_SOFTLEFT;
    }

    private void DetectSoftRightKeyCode()
    {
        if ( m_platform != PLATFORM_UNKNOWN )
        {
            String keyWord = "SOFT";
            int i;

            for ( i = 0; i != SOFTRIGHT_KEYCODES[ m_platform ].length; ++i )
            {
                try
                {
                    if ( this.getKeyName(
                        SOFTRIGHT_KEYCODES[ m_platform ][ i ] ).toUpperCase().
                        indexOf( keyWord ) != -1 )
                    {
                        m_softrightKeyCode =
                            SOFTRIGHT_KEYCODES[ m_platform ][ i ];
                        return;
                    }
                } catch ( IllegalArgumentException e ) {}
            }
        }

        m_softrightKeyCode = KEY_DEFAULT_SOFTRIGHT;
    }

    private static final byte PLATFORM_UNKNOWN  = -1;
    private static final byte PLATFORM_NOKIA    = 0;
    private static final byte PLATFORM_MOTOROLA = 1;
    private static final byte PLATFORM_SAMSUNG  = 2;
    private static final byte PLATFORM_SE       = 3;
    private static final byte PLATFORM_SIEMENS  = 4;
    private static final byte PLATFORM_LG       = 5;

    private static final int[][] SOFTLEFT_KEYCODES = {
        { -6 },                     // Nokia
        { -21, 21, -20, -11, -7 },  // Motorola
        { -6, 21, -7, -8 },         // Samsung
        { -6 },                     // Sony Ericsson
        { -1, 105 },                // Siemens
        { -6, -202, -20 }           // LG
    };

    private static final int[][] SOFTRIGHT_KEYCODES = {
        { -7 },                 // Nokia
        { -21, -22, 22, -8 },   // Motorola
        { -7, 22, -6, -7 },     // Samsung
        { -7 },                 // Sony Ericsson
        { -4, 106 },            // Siemens
        { -7, -203, -21 }       // LG
    };

    // Default key codes below are usually used by "unusual" devices and
    // emulators (I'm not sure, let's assume that it's right).
    private static final int KEY_DEFAULT_SOFTLEFT   = -6;
    private static final int KEY_DEFAULT_SOFTRIGHT  = -7;

    private byte[]  m_behaviourList;

    private long    m_keyCooldown, m_keyLatency, m_keyLastTime,
                    m_touchHoldLastTime;    // Last time holding touch without
                                            // moving or releasing.
    private int     m_actionBitPattern, m_pointerPosition,
                    m_touchSimulatedActions,
                    m_producedActions;
    private int     m_softleftKeyCode, m_softrightKeyCode;
    private int     m_inputMode;
    private short   m_pressedX, m_pressedY;
    private boolean m_touchSKNotExecuted,
                    m_touchHolding; // User touchs without dragging.
    private byte    m_platform;
}
