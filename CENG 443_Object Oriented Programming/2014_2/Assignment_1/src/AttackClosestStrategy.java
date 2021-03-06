//
//
//  Generated by StarUML(tm) Java Add-In
//
//  @ Project : Untitled
//  @ File Name : AttackClosestStrategy.java
//  @ Date : 13.3.2015
//  @ Author : 
//
//




public class AttackClosestStrategy extends Strategy {
    Team enemyTeam;
    Knight target = null;
    float rangedAttackProb = 0.01f;
    public AttackClosestStrategy(Knight owner) {
        super(owner);
        
        if (owner.getTeam() == Simulation.Instance().teams[0])
            enemyTeam = Simulation.Instance().teams[1];
        else
            enemyTeam = Simulation.Instance().teams[0];
        target = enemyTeam.get(0);
        getClosest();
    }

    @Override
    public void act() {
        getClosest();
        owner.moveTo( target.getX(), target.getY());
        if (rangedAttackProb > rng.nextDouble())
            owner.rangedAttack();
    }

    private void getClosest() {
         if (target == null)
            target = enemyTeam.get(0);
        float dist = getDist(target);
        for (int i = 0; i<enemyTeam.size(); i++ )
            if (enemyTeam.get(i)!=null && dist > getDist(enemyTeam.get(i))) {
                target = enemyTeam.get(i);
                dist = getDist(enemyTeam.get(i));
            }
    }
    private float getDist (Knight target) {
        return (target.getX() - owner.getX()) * (target.getX() - owner.getX()) + (target.getY() - owner.getY()) * (target.getY() - owner.getY()); 
    }
    
    
}
