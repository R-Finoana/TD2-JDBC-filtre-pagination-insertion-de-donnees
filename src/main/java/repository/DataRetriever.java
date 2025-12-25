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
import java.util.Objects;
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
                    team = new Team(
                            rs.getInt("team_id"),
                            rs.getString("team_name"),
                            ContinentEnum.valueOf(rs.getString("continent")),
                            players
                    );
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
        List<Player> players = new ArrayList<>();

        int offset = (page-1) * size;
        String sql = """
                SELECT player.id AS player_id, player.name AS player_name, player.age, player.position, team.id AS team_id, team.name AS team_name, team.continent
                FROM player
                LEFT JOIN team
                ON player.id_team = team.id
                ORDER BY player_id
                LIMIT ? OFFSET ?
                """;

        try(Connection conn = DBConnection.getDBConnection(); PreparedStatement stmt = conn.prepareStatement(sql);){

            stmt.setInt(1, size);
            stmt.setInt(2, offset);

            ResultSet rs = stmt.executeQuery();

            while(rs.next()){
                Team team = null;
                if(rs.getObject("team_id") != null){
                    team = new Team(rs.getInt("team_id"), rs.getString("team_name"), ContinentEnum.valueOf(rs.getString("continent")), new ArrayList<>());
                }

                players.add(new Player(
                        rs.getInt("player_id"),
                        rs.getString("player_name"),
                        rs.getInt("age"),
                        PlayerPositionEnum.valueOf(rs.getString("position")),
                        team
                ));
            }
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
        return players;
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
        List<Player> players = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
                SELECT player.id AS player_id,
                player.name AS player_name,
                player.age,
                player.position AS player_position,
                team.id AS id_team,
                team.name AS team_name,
                team.continent AS team_continent
                FROM player
                LEFT JOIN team
                ON team.id = player.id_team
                WHERE 1=1
                """);

        List<Object> params = new ArrayList<>();

        if(playerName != null && !playerName.trim().isEmpty()){
            sql.append(" AND player.name ILIKE ? ");
            params.add("%"+playerName+"%");
        }

        if(position != null){
            sql.append(" AND player.position = ?::\"position\" ");
            params.add(position.name());
        }

        if(continent != null){
            sql.append(" AND team.continent = ?::continent");
            params.add(continent.name());
        }

        if(teamName != null && !teamName.trim().isEmpty()){
            sql.append((" AND team.name ILIKE ?"));
            params.add("%"+teamName+"%");
        }

        sql.append(" ORDER BY player.id");
        sql.append(" LIMIT ? OFFSET ?");
        params.add(size);
        params.add((page-1)*size);

        try(Connection conn = DBConnection.getDBConnection();
            PreparedStatement stmt = conn.prepareStatement(sql.toString())){

            for(int i = 0; i<params.size(); i++){
                stmt.setObject(i+1, params.get(i));
            }

            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                Team team = null;
                if(rs.getObject("id_team") != null){
                    team = new Team(
                            rs.getInt("id_team"),
                            rs.getString("team_name"),
                            ContinentEnum.valueOf(rs.getString("team_continent")),
                            new ArrayList<>()
                    );
                }

                players.add(new Player(
                        rs.getInt("player_id"),
                        rs.getString("player_name"),
                        rs.getInt("age"),
                        PlayerPositionEnum.valueOf(rs.getString("player_position")),
                        team
                ));
            }
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
        return players;
    }
}
