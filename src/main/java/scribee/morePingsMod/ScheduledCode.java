package scribee.morePingsMod;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ScheduledCode {
	
	public Runnable code;
	public int ticks;
	
	public ScheduledCode (Runnable codeToRun, int ticksToWait) {
		code = codeToRun;
		ticks = ticksToWait;
		
		init();
	}
	
	public void init() {
        MinecraftForge.EVENT_BUS.register(this);
	}
	
	@SubscribeEvent
    public void onTickEvent(TickEvent.ClientTickEvent event) {
        if (ticks == 0) {
            code.run();
            
            MinecraftForge.EVENT_BUS.unregister(this);
        }
        else {
        	ticks--;
        }
    }
}
