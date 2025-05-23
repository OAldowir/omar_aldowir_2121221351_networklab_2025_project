package com.mycompany.omar_aldowir_2121221351_networklab_2025_project;

public class Omar_ALDOWIR_2121221351_networklab_2025_project {

    public static void main(String[] args) {
        Player p1 = new Player("Player 1");
        Player p2 = new Player("Player 2");
        GameState game = new GameState(p1, p2);

        System.out.println("----- Region Distribution -----");
        for (Region r : game.getRegions()) {
            System.out.println("Region " + r.getName() + " is owned by "
                    + r.getOwner().getName() + " and contains " + r.getSoldiers() + " soldiers.");
        }

        System.out.println("\n----- Attack Test -----");
        Region attacker = p1.getRegions().get(0);
        Region target = p2.getRegions().get(0);
        String result = game.attack(attacker, target);
        System.out.println(result);

        System.out.println("\n----- Reinforcement Test -----");
        Region testRegion = p1.getRegions().get(0);
        if (p1.canReinforce()) {
            testRegion.addSoldiers(1);
            p1.useReinforcement();
            System.out.println("Region " + testRegion.getName()
                    + " has been reinforced. Now contains " + testRegion.getSoldiers() + " soldiers.");
        }

        System.out.println("\n----- Troop Movement Test -----");
        Region from = p1.getRegions().get(0);
        Region to = p1.getRegions().get(1);
        String moveResult = game.moveTroops(from, to, 2);
        System.out.println(moveResult);

        game.endTurn();
        System.out.println("\n----- End of Turn -----");
        System.out.println("Now it's: " + game.getCurrentTurn().getName() + "'s turn");
    }

}
