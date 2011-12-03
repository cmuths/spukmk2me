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

package com.spukmk2me.input;

/**
 *  Wrapper for input management.
 *  \details Since there is some quite common problems happen when developing
 * games on J2ME platform using MIDP API, so I decided to put up a wrapper for
 * input management. Hope that helps.
 */
public interface IInputMonitor
{    
    /**
     *  Setup the input receiving mode.
     *  \details You can combine the input mode from INPUTMODE_KEY and
     * INPUTMODE_TOUCH. Or even disable all input signal via the InputMonitor
     * by passing INPUTMODE_NONE.
     *  @param inputMode The input mode.
     */
    public void SetInputMode( int inputMode );

    /**
     *  Get the input capability.
     *  @return Returned value is combined from INPUTMODE_NONE, INPUTMODE_KEY
     * and INPUTMODE_TOUCH.
     */
    public int GetInputCapability();

    /**
     *  Setup the receiving behaviour.
     *  @param action Input action that will be affected.
     *  @param behaviour New behaviour of input action.
     */
    public void SetInputBehaviour( int action, byte behaviour );

    /**
     *  Get input states at the current moment.
     *  \details After this call, states of all actions will be changed
     * according to the input behaviour of each action.
     *  @return The bit pattern for actions. See the constants list for more
     * details.
     */
    public int GetInputStates();
    
    /**
     *  Check if the specific action is enabled.
     *  \details This function uses data from the last call to GetInputState()
     * to check. If there's no GetInputState() call prior to this function,
     * every actions are assumed to not happen.
     *  @param action Action to check.
     *  @return true if the specific action is enabled, otherwise return
     * false.
     */
    public boolean Acted( int action );
    
    /**
     *  Get the touch position.
     *  \details The X coordinate will be stored at the higher word of returned
     * value, Y coordinate will be the lower word. The value 0xFFFFFFFF will be
     * returned if there is no touching action.
     *  @return The data for touching position.
     */
    public int GetTouchingPosition();

    /**
     *  Change the key input latency.
     *  \details Latency? What's that?\n
     *  Have you ever suffered this situation: when you've just pressed
     * the "right" button, your character immediately moves at
     * super-man speed to the enemies and died in a good way? That's because
     * you didn't time-based simulate your game well enough. But there is
     * another minor cause: you catch the key signals too frequently, or even
     * catch them as much as possible in every frame. That why this
     * "key latency" was born.
     *  @param latency The key latency, measured in milliseconds.
     */
    public void SetLatency( long latency );

    /**
     *  Set the latency that has effect only once.
     *  \details Isn't like SetLatency(), this latency here is only
     * temporary. To be more specific, let's say your games are setting
     * the key latency to 100ms, but after players press "Quit" button, you
     * want to cutoff the key input for a little while, e.g. with 500ms latency
     * so players don't accidently press the next
     * "YEEES, I really want to quit this stupid game" button (100ms is still
     * little too fast, you know). You want 500ms latency has only one turn of
     * effect, right. If that's true, so use this function.
     *  @param latency The temporary latency, measured in milliseconds.
     */
    public void SetTemporaryLatency( long latency );    

    public static final int ACT_NONE    = 0x00000000; //!< Nothing.
    public static final int ACT_UP      = 0x00000001; //!< Up or Numpad 2
    public static final int ACT_LEFT    = 0x00000002; //!< Left or Numpad 4
    public static final int ACT_DOWN    = 0x00000004; //!< Right or Numpad 8
    public static final int ACT_RIGHT   = 0x00000008; //!< Left or Numpad 6
    public static final int ACT_FIRE    = 0x00000010; //!< Mid or Numpad 5
    public static final int ACT_LSOFT   = 0x00000020; //!< Left button
    public static final int ACT_RSOFT   = 0x00000040; //!< Right button
    public static final int ACT_NUM0    = 0x00000080; //!< Numpad 0
    public static final int ACT_NUM1    = 0x00000100; //!< Numpad 1
    public static final int ACT_NUM2    = 0x00000200; //!< Numpad 2
    public static final int ACT_NUM3    = 0x00000400; //!< Numpad 3
    public static final int ACT_NUM4    = 0x00000800; //!< Numpad 4
    public static final int ACT_NUM5    = 0x00001000; //!< Numpad 5
    public static final int ACT_NUM6    = 0x00002000; //!< Numpad 6
    public static final int ACT_NUM7    = 0x00004000; //!< Numpad 7
    public static final int ACT_NUM8    = 0x00008000; //!< Numpad 8
    public static final int ACT_NUM9    = 0x00010000; //!< Numpad 9
    public static final int ACT_NUMPND  = 0x00020000; //!< Numpad #
    public static final int ACT_NUMSTAR = 0x00040000; //!< Numpad *

    //! Action bit is enabled when the corresponding key is held.
    //! \details This is the default behaviour. Action bit is enabled if and
    //! only if the key is held.
    public static final byte BHV_ENABLE_WHEN_HOLDING    = 0;

    //! Action bit is enabled when the corresponding key is pressed (once).
    //! \details The action bit is enabled only when the key is pressed. After
    //! that, from the next call to GetActionBitPattern(), the bit will be
    //! disabled even the key is held. It is enabled again if and only if the
    //! key is pressed again.
    //! Key releasing will (of course), disable the action bit.
    public static final byte BHV_ENABLE_ON_FIRST_PRESS  = 1;

    //! Action bit will be enabled once when the corresponding key is pressed.
    //! \details This behaviour is just like BHV_ENABLE_ON_FIRST_PRESS, the
    //! action bit is enabled once. The difference is: key releasing is
    //! ignored. This behaviour is created originaly to work around
    //! Nokia X3-02 devices, which send "key released" (R) right after
    //! "key pressed" (P) signal if user press softkeys.
    public static final byte BHV_FIX_DOUBLE_SIGNAL_PR   = 2;

    public static final byte INPUTMODE_NONE     = 0x00; //!< Disable the input.
    public static final byte INPUTMODE_KEY      = 0x01; //!< Accept keys.
    public static final byte INPUTMODE_TOUCH    = 0x02; //!< Accept touching.

    //! Generate ACT_LEFT, ACT_RIGHT, ACT_UP, ACT_DOWN and ACT_FIRE by
    //! touching. Touching mode must be enabled. ACT_FIRE is generated by
    //! double clicking, while ACT_UP, ACT_DOWN, ACT_LEFT and ACT_RIGHT are
    //! generated by sliding your stylus (or finger).
    //! In this mode, BHV_FIX_DOUBLE_SIGNAL_PR is recommended.
    public static final byte INPUTMODE_TOUCH_SK = 0x04;

    //! Generate ACT_LSOFT, ACT_RSOFT, ACT_FIRE by touching on bottom area
    //! of screen (fix Nokia Series 40). Touching mode must be enabled. This
    //! mode supresses INPUTMODE_TOUCH_SK.
    //! The emulator included in "Series 40 6th Edition SDK Feature Pack 1",
    //! which is released by Nokia shown that when your Canvas override
    //! touching functions (like pointerPressed()), the device automatically
    //! change to full touching mode, which won't generate FIRE, LSOFT and
    //! RSOFT keycode. Enabling this patch only simulate the behaviour of
    //! S40 devices: when the bottom of screen is touched
    //! (key simulation mode), the devices will generate two signals:
    //! key pressed following by key released. So you must define
    //! BHV_FIX_DOUBLE_SIGNAL_PR to ACT_FIRE, ACT_LSOFT, ACT_RSOFT in addition
    //! to input mode setting.
    public static final byte INPUTMODE_TOUCH_SK_FIX_NOKIAS40 = 0x08;
}
