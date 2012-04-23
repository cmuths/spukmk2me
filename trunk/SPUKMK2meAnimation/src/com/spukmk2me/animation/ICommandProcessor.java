package com.spukmk2me.animation;

public interface ICommandProcessor
{
    public boolean IsSupported( int commandCode );
    public int Process( Command command );
    public void Execute( int deltaTime );
}
