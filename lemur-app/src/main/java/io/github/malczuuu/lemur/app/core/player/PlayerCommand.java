package io.github.malczuuu.lemur.app.core.player;

import io.github.malczuuu.lemur.app.core.Command;

public interface PlayerCommand extends Command {

  record CreatePlayer(String name) {}

  record UpdatePlayer(String id, String name, Long version) {}

  record BanPlayer(String id) {}

  record UnbanPlayer(String id) {}
}
