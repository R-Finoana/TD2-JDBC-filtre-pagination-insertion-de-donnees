import model.ContinentEnum;
import model.Player;
import model.PlayerPositionEnum;
import model.Team;
import repository.DataRetriever;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Main {
    public static void main() {
        DataRetriever dr = new DataRetriever();

        System.out.println("=== Find team by id ===");
        Optional<Team> teamById = dr.findTeamById(1);

        if(teamById.isPresent()){
            Team team = teamById.get();
            System.out.println(team.getName()+" "+team.getContinent());

            System.out.println("Players list :");
            for (Player player : team.getPlayers()) {
                System.out.println("- " + player.getName() +
                        " (age: " + player.getAge() +
                        ", position: " + player.getPosition() + ")");
            }
        } else{
            System.out.println("No team found");
        }

        System.out.println("\n=== Players list ===");
        System.out.println("-- Page 1, size 2 --");
        dr.findPlayers(1, 2).forEach(System.out::println);
        System.out.println("-- Page 3, size 5 --");
        dr.findPlayers(3, 5).forEach(System.out::println);

        System.out.println("\n=== Find Player by Criteria ===");
        dr.findPlayersByCriteria("ud", PlayerPositionEnum.MIDF, "Madrid", ContinentEnum.EUROPA, 1, 10).forEach(System.out::println);


        System.out.println("\n=== Create players with a raised exception ===");
        List<Player> duplicatedPlayers = List.of(
                new Player(6, "Jude Bellingham", 23, PlayerPositionEnum.STR, null),
                new Player(7, "Pedri", 24, PlayerPositionEnum.MIDF, null)
        );

        try{
            dr.createPlayers(duplicatedPlayers);
            System.out.println("No Exception raised");
        } catch (RuntimeException e){
            throw new RuntimeException(e.getMessage());
        }
    }
}
