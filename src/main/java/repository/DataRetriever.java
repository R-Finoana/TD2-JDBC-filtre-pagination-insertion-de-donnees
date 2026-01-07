package repository;

import connection.DBConnection;
import model.ContinentEnum;
import model.Player;
import model.PlayerPositionEnum;
import model.Team;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

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
                            rs.getInt("goal_nb"),
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
                        rs.getInt("goal_nb"),
                        team
                ));
            }
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
        return players;
    }

    public List<Player> createPlayers(List<Player> newPlayers){
        if(newPlayers.isEmpty()){
            return new ArrayList<>();
        }

        String checkSql = "SELECT id FROM player WHERE id = ?";
        String insertSql = """
                INSERT INTO player (id, name, age, position, id_team)
                VALUES (?, ?, ?, ?::"position", ?)
                """ ;

        try(Connection conn = DBConnection.getDBConnection()){
            conn.setAutoCommit(false);

            try(
                    PreparedStatement checkStmt = conn.prepareStatement(checkSql);
                    PreparedStatement insertStmt = conn.prepareStatement(insertSql);
            ){
                for(Player player : newPlayers){
                    checkStmt.setInt(1, player.getId());

                    ResultSet rs = checkStmt.executeQuery();
                    while(rs.next()){
                        throw new RuntimeException("Player with id "+player.getId()+" already exists");
                    }

                    insertStmt.setInt(1, player.getId());
                    insertStmt.setString(2, player.getName());
                    insertStmt.setInt(3, player.getAge());
                    insertStmt.setString(4, player.getPosition().name());
                    if(player.getTeam() != null){
                        insertStmt.setNull(5, player.getTeam().getId());
                    } else{
                        insertStmt.setInt(5, Types.INTEGER);
                    }
                    insertStmt.executeUpdate();
                }
                conn.commit();
                return new ArrayList<>(newPlayers);
            } catch (Exception e) {
                conn.rollback();
                throw new RuntimeException(e);
            }
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public Team saveTeam(Team teamToSave){
        if(teamToSave == null){
            throw new IllegalArgumentException("Team to save cannot be null");
        }

            String checkTeamSql = "SELECT id FROM team WHERE id = ?";
            String insertTeamSql = "INSERT INTO team (id, name, continent) VALUES (?, ?, ?::continent)";
            String updateTeamSql = "UPDATE team SET name = ?, continent = ?::continent WHERE id = ?";

            String insertPlayerSql = "INSERT INTO player (id, name, age, position, id_team) VALUES (?, ?, ?, ?::position, ?)";
            String updatePlayerSql = "UPDATE player SET name = ?, age = ?, position = ?::position, id_team = ? WHERE id = ?";

            try (Connection conn = DBConnection.getDBConnection()) {
                conn.setAutoCommit(false);

                try {
                    boolean teamExists;
                    try (PreparedStatement checkStmt = conn.prepareStatement(checkTeamSql)) {
                        checkStmt.setInt(1, teamToSave.getId());
                        ResultSet rs = checkStmt.executeQuery();
                        teamExists = rs.next();
                    }

                    if (teamExists) {
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateTeamSql)) {
                            updateStmt.setString(1, teamToSave.getName());
                            updateStmt.setString(2, teamToSave.getContinent().name());
                            updateStmt.setInt(3, teamToSave.getId());
                            updateStmt.executeUpdate();
                        }
                    } else {
                        try (PreparedStatement insertStmt = conn.prepareStatement(insertTeamSql)) {
                            insertStmt.setInt(1, teamToSave.getId());
                            insertStmt.setString(2, teamToSave.getName());
                            insertStmt.setString(3, teamToSave.getContinent().name());
                            insertStmt.executeUpdate();
                        }
                    }

                    Team savedTeam = new Team(
                            teamToSave.getId(),
                            teamToSave.getName(),
                            teamToSave.getContinent(),
                            new ArrayList<>()
                    );

                    List<Player> players = teamToSave.getPlayers() != null ? teamToSave.getPlayers() : Collections.emptyList();

                    if (!players.isEmpty()) {
                        String ids = players.stream().map(p -> String.valueOf(p.getId())).collect(Collectors.joining(", "));
                        String detachSql = "UPDATE player SET id_team = NULL WHERE id_team = ? AND id NOT IN (" + ids + ")";
                        try (PreparedStatement stmt = conn.prepareStatement(detachSql)) {
                            stmt.setInt(1, savedTeam.getId());
                            stmt.executeUpdate();
                        }
                    } else {
                        try (PreparedStatement stmt = conn.prepareStatement("UPDATE player SET id_team = NULL WHERE id_team = ?")) {
                            stmt.setInt(1, savedTeam.getId());
                            stmt.executeUpdate();
                        }
                    }

                    for (Player player : players) {
                        player.setTeam(savedTeam);

                        boolean playerExists;
                        try (PreparedStatement check = conn.prepareStatement("SELECT id FROM player WHERE id = ?")) {
                            check.setInt(1, player.getId());
                            playerExists = check.executeQuery().next();
                        }

                        if (playerExists) {
                            try (PreparedStatement update = conn.prepareStatement(updatePlayerSql)) {
                                update.setString(1, player.getName());
                                update.setInt(2, player.getAge());
                                update.setString(3, player.getPosition().name());
                                update.setInt(4, savedTeam.getId());
                                update.setInt(5, player.getId());
                                update.executeUpdate();
                            }
                        } else {
                            try (PreparedStatement insert = conn.prepareStatement(insertPlayerSql)) {
                                insert.setInt(1, player.getId());
                                insert.setString(2, player.getName());
                                insert.setInt(3, player.getAge());
                                insert.setString(4, player.getPosition().name());
                                insert.setInt(5, savedTeam.getId());
                                insert.executeUpdate();
                            }
                        }
                        savedTeam.getPlayers().add(player);
                    }

                    conn.commit();
                    return savedTeam;

                } catch (Exception e) {
                    conn.rollback();
                    throw new RuntimeException(e);
                }
            }
            catch (SQLException e){
                throw new RuntimeException(e);
        }
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
                        rs.getInt("goal_nb"),
                        team
                ));
            }
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
        return players;
    }
}
