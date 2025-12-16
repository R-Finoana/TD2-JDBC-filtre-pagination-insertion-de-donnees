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
                SELECT team.id, team.name, team.continent, player.id, player.name, player.position
                FROM team
                LEFT JOIN player
                ON team.id = player.id_team
                WHERE team.id = ?;
                """;
        List<Player> players = new ArrayList<>();
        Team team = new Team();

        try(Connection conn = DBConnection.getDBConnection();){
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()){

                Player player = null;
                if(rs.getObject("id") != null){
                    int playerId = rs.getInt("id");
                    String name = rs.getString("name");
                    int age = rs.getInt("age");
                    String position = rs.getString("position");
                    player = new Player(playerId, name, age, PlayerPositionEnum.valueOf(position), team);
                }
                for (int i =0; i<players.size(); i++){
                    players.get(i).setTeam(team);
                }
                players.add(player);
                Team newTeam = new Team(rs.getInt("id"), rs.getString("name"), ContinentEnum.valueOf(rs.getString("continent")), players);
                team = newTeam;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.of(team);
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
