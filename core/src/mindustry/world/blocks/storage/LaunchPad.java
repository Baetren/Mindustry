package mindustry.world.blocks.storage;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.meta.*;

import static mindustry.Vars.*;

public class LaunchPad extends StorageBlock{
    public final int timerLaunch = timers++;
    /** Time inbetween launches. */
    public float launchTime;

    public LaunchPad(String name){
        super(name);
        update = true;
        hasItems = true;
        solid = true;
    }

    @Override
    public void setStats(){
        super.setStats();

        stats.add(BlockStat.launchTime, launchTime / 60f, StatUnit.seconds);
    }

    public class LaunchPadEntity extends StorageBlockEntity{
        @Override
        public void draw(){
            super.draw();

            //TODO broken
            float progress = Mathf.clamp(Mathf.clamp((items.total() / (float)itemCapacity)) * ((timer().getTime(timerLaunch) / (launchTime / timeScale()))));
            float scale = size / 3f;

            Lines.stroke(2f);
            Draw.color(Pal.accentBack);
            Lines.poly(x, y, 4, scale * 10f * (1f - progress), 45 + 360f * progress);

            Draw.color(Pal.accent);

            if(cons.valid()){
                for(int i = 0; i < 3; i++){
                    float f = (Time.time() / 200f + i * 0.5f) % 1f;

                    Lines.stroke(((2f * (2f - Math.abs(0.5f - f) * 2f)) - 2f + 0.2f));
                    Lines.poly(x, y, 4, (1f - f) * 10f * scale);
                }
            }

            Draw.reset();
        }

        @Override
        public boolean acceptItem(Tilec source, Item item){
            return item.type == ItemType.material && items.total() < itemCapacity;
        }

        @Override
        public void updateTile(){

            if(state.isCampaign() && consValid() && items.total() >= itemCapacity && timer(timerLaunch, launchTime / timeScale())){
                for(Item item : Vars.content.items()){
                    Events.fire(Trigger.itemLaunch);
                    Fx.padlaunch.at(tile);
                    int used = Math.min(items.get(item), itemCapacity);
                    data.addItem(item, used);
                    items.remove(item, used);
                    Events.fire(new LaunchItemEvent(item, used));
                }
            }
        }
    }
}
