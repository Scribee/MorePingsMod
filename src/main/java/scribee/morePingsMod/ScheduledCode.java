package scribee.morePingsMod;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ScheduledCode {
	
	public Runnable method;
	public int ticks;
	
	public ScheduledCode (Runnable method, int ticksToWait) {
		this.method = method;
		ticks = ticksToWait;
		
		init();
	}
	
	public void init() {
        MinecraftForge.EVENT_BUS.register(this);
	}
	
	@SubscribeEvent
    public void onTickEvent(TickEvent.ClientTickEvent event) {
        if (ticks == 0) {
            method.run();
            
            MinecraftForge.EVENT_BUS.unregister(this);
        }
        else {
        	ticks--;
        }
    }
}
