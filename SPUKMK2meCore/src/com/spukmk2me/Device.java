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

package com.spukmk2me;

import com.spukmk2me.io.IFileSystem;
import com.spukmk2me.input.IInputMonitor;
import com.spukmk2me.video.IVideoDriver;
import com.spukmk2me.sound.ISoundMonitor;
import com.spukmk2me.scene.SceneManager;

/**
 *  @package com.spukmk2me
 *  SPUKMK2me is a simple game engine (it's still an immature
 * rendering engine) written for J2ME platform by HNYD Team.
 */

/**
 *  @package com.spukmk2me.debug
 *  Contains debug-related classes. This package exists in debug version only.
 */

/**
 *  @package com.spukmk2me.scene
 *  Hold everything about scene managing.
 */

/**
 *  @package com.spukmk2me.scene.complex
 *  Some advanced scene nodes, can be somewhat called "complex" nodes.
 */

/**
 *  @package com.spukmk2me.video
 *  Provide interface to interact with other rendering API.
 */

/**
 *  @package com.spukmk2me.gameflow
 *  Pre-setup classes for building games with SPUKMK2me.
 */

/**
 *  @package com.spukmk2me.extension.midp
 *  SPUKMK2me Engine - MIDP extension package.
 */

/**
 *  @package com.spukmk2me.extension.gles
 *  SPUKMK2me Engine - OpenGL ES extension package.
 */

/**
 *  The main device which hold everything of SPUKMK2me rendering engine.
 *  \details To access SPUKMK2me rendering engine, first you must create an
 * instance of Device class, and access things via this instance.\n
 *  There should be only one instance of Device at the same time
 * in your application. If there are more than one device, the result is
 * unpredictable.
 *  @see com.spukmk2me.scene.SceneManager
 *  @see com.spukmk2me.video.IVideoDriver
 *  @see com.spukmk2me.sound.ISoundMonitor
 *  @see com.spukmk2me.IInputMonitor
 */
public final class Device
{
    private Device( IVideoDriver vdriver, ISoundMonitor smonitor,
        IInputMonitor inputMonitor, IFileSystem fileSystem, SceneManager scene )
    {
        m_soundMonitor  = smonitor;
        m_videoDriver   = vdriver;
        m_inputMonitor  = inputMonitor;
        m_sceneManager  = scene;
        m_fileSystem    = fileSystem;
    }

    /**
     *  Setup a device manually. Not recommended for new developer.
     *  \details If you can manually setup all the components, you can forget
     * about Device object and this function as well.
     *  @param vdriver Video driver.
     *  @param smonitor Sound monitor.
     *  @param imonitor Input monitor.
     *  @param scene Scene manager.
     *  @return Device that hold all the components above.
     */
    public static Device CreateSPUKMK2meDevice(
        IVideoDriver vdriver, ISoundMonitor smonitor,
        IInputMonitor imonitor, IFileSystem fsystem, SceneManager scene )
    {
        return new Device( vdriver, smonitor, imonitor, fsystem, scene );
    }

    /**
     *  Get the current video driver.
     *  @return The current video driver.
     */
    public IVideoDriver GetVideoDriver()
    {
        return m_videoDriver;
    }
    
    /**
     *  Get the current sound monitor.
     *  @return The current sound monitor.
     */
    public ISoundMonitor GetSoundMonitor()
    {
        return m_soundMonitor;
    }
    
    /**
     *  Get the current input monitor.
     *  @return The current input monitor.
     */
    public IInputMonitor GetInputMonitor()
    {
        return m_inputMonitor;
    }
    
    /**
     *  Get the current file system.
     *  @return The current file system.
     */
    public IFileSystem GetFileSystem()
    {
        return m_fileSystem;
    }

    /**
     *  Get the current scene manager.
     *  @return The current scene manager.
     */
    public SceneManager GetSceneManager()
    {
        return m_sceneManager;
    }

    /**
     *  Stop the device and release all resource associated with it.
     *  \details Since the resource are managed by Java Virtual Machine,
     * all this function can do is nullify all references.
     */
    public void StopDevice()
    {
        if ( m_soundMonitor != null )
            m_soundMonitor.Clear();
        
        m_videoDriver   = null;
        m_sceneManager  = null;
        m_inputMonitor  = null;
        m_soundMonitor  = null;
    }

    private IVideoDriver    m_videoDriver;
    private SceneManager    m_sceneManager;
    private IInputMonitor   m_inputMonitor;
    private ISoundMonitor   m_soundMonitor;
    private IFileSystem     m_fileSystem;
}
