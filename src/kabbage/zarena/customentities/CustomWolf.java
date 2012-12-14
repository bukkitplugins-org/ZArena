package kabbage.zarena.customentities;

import java.lang.reflect.Field;

import kabbage.customentitylibrary.CustomEntityMoveEvent;
import kabbage.customentitylibrary.CustomEntityWrapper;
import net.minecraft.server.DamageSource;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityAgeable;
import net.minecraft.server.EntityAnimal;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityWolf;
import net.minecraft.server.ItemStack;
import net.minecraft.server.PathfinderGoalFloat;
import net.minecraft.server.PathfinderGoalHurtByTarget;
import net.minecraft.server.PathfinderGoalLeapAtTarget;
import net.minecraft.server.PathfinderGoalLookAtPlayer;
import net.minecraft.server.PathfinderGoalMeleeAttack;
import net.minecraft.server.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.PathfinderGoalRandomLookaround;
import net.minecraft.server.PathfinderGoalRandomStroll;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.util.UnsafeList;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

public class CustomWolf extends EntityWolf
{
	private EntityTypeConfiguration type;
	private CustomWolf(World world, Location location, EntityTypeConfiguration type)
	{
		super(((CraftWorld) world).getHandle());
		this.setPosition(location.getX(), location.getY(), location.getZ());
		this.type = type;

        this.a(0.6F, 0.8F);
	}

	@Override
    public boolean m(Entity entity) {
        int damage = 2;
        return entity.damageEntity(DamageSource.mobAttack(this), damage);
    }

    @Override
    public boolean a(EntityHuman entityhuman)
    {
        return false;	//No feeding/taming this wolf
    }

    @Override
    public boolean c(ItemStack itemstack)
    {
    	return false;	//This wolf needs no food
    }

    @Override
    public boolean isAngry()
    {
        return true;	//Let the anger flow through you
    }

    @Override
    public void setAngry(boolean flag)
    {
        //Redundant, this wolf is always angry
    }

    @Override
    public EntityAgeable createChild(EntityAgeable entityanimal)
    {
        return null;
    }

    @Override
    public boolean mate(EntityAnimal entityanimal)
    {
        return false;	//The war is your mate!
    }
	
	@Override
	public void move(double d0, double d1, double d2)
	{
		CustomEntityMoveEvent event = new CustomEntityMoveEvent(this.getBukkitEntity(), new Location(this.world.getWorld(), lastX, lastY, lastZ), new Location(this.world.getWorld(), locX, locY, locZ));
		Bukkit.getServer().getPluginManager().callEvent(event);
		if(event.isCancelled())
			this.setPosition(lastX, lastY, lastZ);
		else
			super.move(d0, d1, d2);
	}
	
	@SuppressWarnings("rawtypes")
	private void resetPathfinders()
	{
        try
		{
			//Enable PathfinderGoalSelector's "a" field to be editable
			Field gsa = net.minecraft.server.PathfinderGoalSelector.class.getDeclaredField("a");
			gsa.setAccessible(true);

			//Now take the instances goals/targets and set them as new lists so they can be rewritten
			gsa.set(this.goalSelector, new UnsafeList());
			gsa.set(this.targetSelector, new UnsafeList());
		} catch (NoSuchFieldException | SecurityException  | IllegalArgumentException | IllegalAccessException e)
		{
			e.printStackTrace();
		}
        
        this.goalSelector.a(1, new PathfinderGoalFloat(this));
        this.goalSelector.a(2, this.d);
        this.goalSelector.a(3, new PathfinderGoalLeapAtTarget(this, 0.4F));
        this.goalSelector.a(4, new PathfinderGoalMeleeAttack(this, EntityHuman.class, this.bG, false));
        this.goalSelector.a(5, new PathFinderGoalMoveToEntity(this, EntityHuman.class, this.bG, type.getRange()));
        this.goalSelector.a(7, new PathfinderGoalRandomStroll(this, this.bG));
        this.goalSelector.a(9, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(9, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, false));
		this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, 16.0F, 0, true));
	}
	
	public static CustomEntityWrapper spawn(Location location, EntityTypeConfiguration type)
	{
		CustomWolf ent = new CustomWolf(location.getWorld(), location, type);
		if(((CraftWorld) location.getWorld()).getHandle().addEntity(ent, SpawnReason.CUSTOM))
		{
			CustomEntityWrapper wrapper = CustomEntityWrapper.spawnCustomEntity(ent, location, type);
			ent.resetPathfinders();
			return wrapper;
		}
		return null;
	}
}