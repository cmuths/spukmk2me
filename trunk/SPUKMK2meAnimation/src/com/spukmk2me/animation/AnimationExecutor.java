package com.spukmk2me.animation;

import com.spukmk2me.DoublyLinkedList;

public final class AnimationExecutor
{
    public AnimationExecutor()
    {
        m_cmdProcessors = new DoublyLinkedList();
    }
    
    public void Prepare( Animation animation )
    {
        m_animation     = animation;
        m_cmdItr        = animation.GetCommands().first();
        m_cmdEndItr     = animation.GetCommands().end();
        m_timeLeft      = 0;
        Execute( 0 );
        
        DoublyLinkedList.Iterator prcItr = m_cmdProcessors.first();
        DoublyLinkedList.Iterator prcEnd = m_cmdProcessors.end();
        
        for ( ; !prcItr.equals( prcEnd ); prcItr.fwrd() )
            ((ICommandProcessor)prcItr.data()).Execute( 0 );
    }
    
    public void AddCommandProcessor( ICommandProcessor processor )
    {
        m_cmdProcessors.push_back( processor );
    }
    
    public boolean Execute( int deltaTime )
    {
        DoublyLinkedList.Iterator prcItr = m_cmdProcessors.first();
        DoublyLinkedList.Iterator prcEnd = m_cmdProcessors.end();
        ICommandProcessor processor;
        Command cmd;
        boolean done = false;

        for ( ; !prcItr.equals( prcEnd ); prcItr.fwrd() )
            ((ICommandProcessor)prcItr.data()).Execute( deltaTime );
        
        while ( m_timeLeft <= 0 )
        {
            if ( m_cmdItr.equals( m_cmdEndItr ) )
            {
                done = true;
                break;
            }
            
            cmd = (Command)m_cmdItr.data();
            prcItr = m_cmdProcessors.first();
            
            for ( ; !prcItr.equals( prcEnd ); prcItr.fwrd() )
            {
                processor = (ICommandProcessor)prcItr.data();
                
                if ( processor.IsSupported( cmd.GetCommandCode() ) )
                    m_timeLeft += processor.Process( cmd );
            }
            
            m_cmdItr.fwrd();
        }

        if ( !done )
            m_timeLeft -= deltaTime;
        
        return done;
    }
    
    private Animation                   m_animation;
    private DoublyLinkedList            m_cmdProcessors;
    private DoublyLinkedList.Iterator   m_cmdItr, m_cmdEndItr;
    private int                         m_timeLeft;
}