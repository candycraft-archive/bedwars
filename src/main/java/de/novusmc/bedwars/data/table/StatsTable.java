package de.novusmc.bedwars.data.table;

import de.novusmc.bedwars.data.MySQL;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

public class StatsTable {

    private static final String TABLE = "bedwars_stats";

    private MySQL mySQL;
    private ExecutorService executorService;

    public StatsTable(MySQL mySQL, ExecutorService executorService) {
        this.mySQL = mySQL;
        this.executorService = executorService;

        mySQL.update("CREATE TABLE IF NOT EXISTS `" + TABLE + "` (`id` INT AUTO_INCREMENT, `uuid` VARCHAR(255), `wins` INT, `kills` INT, `deaths` INT, `played_games` INT, PRIMARY KEY (`id`))");
    }

    public void getUserRanking(UUID uuid, Consumer<Integer> consumer) {
        executorService.execute(() -> {
            try {

                int rank = 0;
                ResultSet result = mySQL.query("SELECT * FROM `" + TABLE + "` ORDER BY `wins` DESC");
                while (result.next()) {
                    rank++;
                    if (result.getString("uuid").equals(uuid.toString())) {
                        consumer.accept(rank);
                        return;
                    }
                }

                consumer.accept(-1);

            } catch (SQLException e) {
                e.printStackTrace();
                consumer.accept(-1);
            }
        });
    }

    public void getStats(UUID uuid, Consumer<Stats> consumer) {
        executorService.execute(() -> {
            try {

                ResultSet result = mySQL.query("SELECT * FROM `" + TABLE + "` WHERE `uuid`='" + uuid + "'");
                if (result.next()) {
                    int wins = result.getInt("wins");
                    int kills = result.getInt("kills");
                    int deaths = result.getInt("deaths");
                    int playedGames = result.getInt("played_games");
                    Stats stats = new Stats(uuid, wins, kills, deaths, playedGames);
                    consumer.accept(stats);
                    return;
                }

                consumer.accept(null);

            } catch (SQLException e) {
                e.printStackTrace();
                consumer.accept(null);
            }
        });
    }

    public void setStats(UUID uuid, Stats stats) {
        getStats(uuid, currentStats -> {

            if (currentStats == null) {
                String sql = "INSERT INTO `" + TABLE + "` VALUES (0, '" + uuid + "', " + stats.wins + ", " +
                        stats.kills + ", " + stats.deaths + ", " + stats.playedGames + ")";
                mySQL.update(sql);
            } else {
                String sql = "UPDATE `" + TABLE + "` SET `wins`=" + stats.wins + ", `kills`=" + stats.kills +
                        ", `deaths`=" + stats.deaths + ", `played_games`=" + stats.playedGames + " WHERE `uuid`='" + uuid + "'";
                mySQL.update(sql);
            }

        });
    }

    public void getTopPlayers(int amount, Consumer<List<UUID>> consumer) {
        executorService.execute(() -> {
            try {

                List<UUID> uuids = new ArrayList<>();
                ResultSet result = mySQL.query("SELECT * FROM `" + TABLE + "` ORDER BY `wins` DESC LIMIT " + amount);
                while (result.next()) {
                    uuids.add(UUID.fromString(result.getString("uuid")));
                }
                consumer.accept(uuids);

            } catch (SQLException e) {
                consumer.accept(new ArrayList<>());
                e.printStackTrace();
            }
        });
    }

    @ToString
    @AllArgsConstructor
    public static class Stats {

        @Getter
        @Setter
        private UUID uuid;

        @Getter
        @Setter
        private int wins, kills, deaths, playedGames;

        public static Stats getDefault() {
            return new Stats(null, 0, 0, 0, 0);
        }

        public double getKD() {
            return kills / (double) deaths;
        }

        public double getWinRate() {
            return wins / (double) playedGames;
        }

    }

}
