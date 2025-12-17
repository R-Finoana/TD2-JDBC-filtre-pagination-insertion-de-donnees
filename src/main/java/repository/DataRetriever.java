package repository;

import connection.DBConnection;
import model.ContinentEnum;
import model.Player;
import model.PlayerPositionEnum;
import model.Team;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DataRetriever {
    public Optional<Team> findTeamById(Integer id){
        String sql = """
                SELECT team.id AS team_id, team.name AS team_name, team.continent, player.id AS player_id, player.name AS player_name, player.age, player.position
                FROM team
                LEFT JOIN player
                ON team.id = player.id_team
                WHERE team.id = ?;
                """;
        List<Player> players = new ArrayList<>();
        Team team = null;

        try(Connection conn = DBConnection.getDBConnection(); PreparedStatement stmt = conn.prepareStatement(sql);){
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()){
                if(team == null){
                    team = new Team(rs.getInt("player_id"), rs.getString("player_name"), ContinentEnum.valueOf(rs.getString("continent")), players);
                }

                if(rs.getObject("player_id") != null){
                    Player player = new Player(
                            rs.getInt("player_id"),
                            rs.getString("player_name"),
                            rs.getInt("age"),
                            PlayerPositionEnum.valueOf(rs.getString("position")),
                            team
                    );
                    players.add(player);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(team);
    }

    public List<Player> findPlayers(int page, int size){
        throw new RuntimeException("Not supported yet");
    }

    public List<Player> createPlayers(List<Player> newPlayers){
        throw new RuntimeException("Not supported yet");
    }

    public Team saveTeam(Team teamToSave){
        throw new RuntimeException("Not supported yet");
    }

    public List<Team> findTeamsByPlayerName(String playerName){
        throw new RuntimeException("Not supported yet");
    }

    public List<Player> findPlayersByCriteria(String playerName, PlayerPositionEnum position, String teamName,
                                              ContinentEnum continent, int page, int size){
        throw new RuntimeException("Not supported yet");
    }
}
