package org.bukkit.plugin;

import org.bukkit.Server;
import org.bukkit.command.SimpleCommandMap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SimplePluginManagerTest {
    // TODO: testing listPlugins would also be reasonable, it should be added when possible

    @Test
    void testPluginLoadOrdering() throws UnknownDependencyException, InvalidPluginException {
        /*
         * Plugins: [A, ABC, B, BeforeA, C, D, E, F, G, H]
         *
         * B depends on D
         * C depends on D
         * D depends on A
         * H depends on B
         * ABC depends on [A, B, C]
         * BeforeA must be loaded before A
         */

        Server server = mock(Server.class);
        when(server.getUpdateFolder()).thenReturn("");
        SimpleCommandMap commandMap = mock(SimpleCommandMap.class);
        SimplePluginManager manager = spy(new SimplePluginManager(server, commandMap));

        doReturn(new SimplePluginManager.PluginFiles(
                new HashMap<>(Map.of(
                        "A", Paths.get("plugins/A.jar"),
                        "B", Paths.get("plugins/B.jar"),
                        "C", Paths.get("plugins/C.jar"),
                        "D", Paths.get("plugins/D.jar"),
                        "E", Paths.get("plugins/E.jar"),
                        "F", Paths.get("plugins/F.jar"),
                        "G", Paths.get("plugins/G.jar"),
                        "H", Paths.get("plugins/H.jar"),
                        "ABC", Paths.get("plugins/ABC.jar"),
                        "BeforeA", Paths.get("plugins/BeforeA.jar")
                )),
                new HashMap<>(Map.of(
                        "B", new ArrayList<>(List.of("D")),
                        "ABC", new ArrayList<>(List.of("A", "B", "C")),
                        "C", new ArrayList<>(List.of("D")),
                        "D", new ArrayList<>(List.of("A")),
                        "H", new ArrayList<>(List.of("B"))
                )),
                new HashMap<>(Map.of(
                        "A", new ArrayList<>(List.of("BeforeA"))
                ))
        )).when(manager).listPlugins(any(File.class), any());

        doAnswer(invocation -> {
            File file = invocation.getArgument(0);
            PluginDescriptionFile description = new PluginDescriptionFile(
                    file.getPath().replaceAll("plugins/|\\.jar", ""),
                    "1.0",
                    "test"
            );

            Plugin plugin = mock(Plugin.class);
            when(plugin.getDescription()).thenReturn(description);
            return plugin;
        }).when(manager).loadPlugin(any(File.class));

        // run the test
        Plugin[] result = manager.loadPlugins(mock(File.class));

        // actually test the returns
        Map<String, Integer> indices = new HashMap<>();
        for (int i = 0; i < result.length; i++) {
            indices.put(result[i].getDescription().getName(), i);
        }

        // make sure all plugins are still included
        assertThat(result)
                .extracting(plugin -> plugin.getDescription().getName())
                .containsExactlyInAnyOrder("A", "ABC", "B", "BeforeA", "C", "D", "E", "F", "G", "H");

        // loadbefore
        assertThat(indices.get("BeforeA")).isLessThan(indices.get("A"));

        // dependencies
        assertThat(indices.get("ABC")).isGreaterThan(indices.get("A"));
        assertThat(indices.get("ABC")).isGreaterThan(indices.get("B"));
        assertThat(indices.get("ABC")).isGreaterThan(indices.get("C"));
        assertThat(indices.get("B")).isGreaterThan(indices.get("D"));
        assertThat(indices.get("C")).isGreaterThan(indices.get("D"));
        assertThat(indices.get("D")).isGreaterThan(indices.get("A"));
        assertThat(indices.get("H")).isGreaterThan(indices.get("B"));
    }
}
