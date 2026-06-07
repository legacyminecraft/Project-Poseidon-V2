package org.bukkit.plugin;

import org.bukkit.Server;
import org.bukkit.command.SimpleCommandMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.*;

@ExtendWith(MockitoExtension.class)
public class SimplePluginManagerTest {
    // TODO: testing listPlugins would also be reasonable, it should be added when possible

    /**
     * A ABC B BeforeA C D E F G H
     * ---
     * B depends on D
     * C depends on D
     * D depends on A
     * H depends on B
     * ABC depends on A, B, C
     * BeforeA has `loadbefore: [A]`
     */
    @Test
    public void loadPlugins_testOrdering() throws UnknownDependencyException, InvalidPluginException {
        // a whole lot of setup
        File fileA = createFileMock("plugins/a.jar");
        File fileB = createFileMock("plugins/b.jar");
        File fileC = createFileMock("plugins/c.jar");
        File fileD = createFileMock("plugins/d.jar");
        File fileE = createFileMock("plugins/e.jar");
        File fileF = createFileMock("plugins/f.jar");
        File fileG = createFileMock("plugins/g.jar");
        File fileH = createFileMock("plugins/h.jar");
        File fileABC = createFileMock("plugins/abc.jar");
        File fileBeforeA = createFileMock("plugins/beforea.jar");

        Server server = Mockito.mock(Server.class);
        Mockito.when(server.getUpdateFolder()).thenReturn("");

        SimplePluginManager manager = Mockito.spy(new SimplePluginManager(server, Mockito.mock(SimpleCommandMap.class)));
        Mockito.doReturn(new SimplePluginManager.PluginFiles(
            new HashMap<>(
                Map.of(
                    "A", fileA,
                    "B", fileB,
                    "C", fileC,
                    "D", fileD,
                    "E", fileE,
                    "F", fileF,
                    "G", fileG,
                    "H", fileH,
                    "ABC", fileABC,
                    "BeforeA", fileBeforeA
                )
            ),
            new HashMap<>(
                Map.of(
                    "B", new ArrayList<>(List.of("D")),
                    "ABC", new ArrayList<>(List.of("A", "B", "C")),
                    "C", new ArrayList<>(List.of("D")),
                    "D", new ArrayList<>(List.of("A")),
                    "H", new ArrayList<>(List.of("B"))
                )
            ),
            new HashMap<>(
                Map.of(
                    "A", new ArrayList<>(List.of("BeforeA"))
                )
            )
        )).when(manager).listPlugins(Mockito.any(File.class), Mockito.any());
        Mockito.doAnswer(invocation -> {
            File file = invocation.getArgument(0);

            PluginDescriptionFile description = new PluginDescriptionFile(
              file.getPath()
                    .replaceAll("plugins/|\\.jar", ""),
                "1.0",
                "test"
            );
            Plugin plugin = Mockito.mock(Plugin.class);
            Mockito.when(plugin.getDescription()).thenReturn(description);

            return plugin;
        }).when(manager).loadPlugin(Mockito.any(File.class));

        // run the test
        Plugin[] result = manager.loadPlugins(Mockito.mock(File.class));

        // actually test the returns
        Assertions.assertSame(10, result.length);
        Map<String, Integer> indices = new HashMap<>();
        for (int i = 0; i < result.length; i++) {
            indices.put(result[i].getDescription().getName(), i);
        }

        // make sure all plugins are still included
        Assertions.assertEquals(
            List.of("a", "abc", "b", "beforea", "c", "d", "e", "f", "g", "h"),
            Arrays.stream(result)
                .map(it -> it.getDescription().getName())
                .sorted().toList()
        );

        // loadbefore
        Assertions.assertTrue(indices.get("beforea") < indices.get("a"));

        // dependencies
        Assertions.assertTrue(indices.get("abc") > indices.get("a"));
        Assertions.assertTrue(indices.get("abc") > indices.get("b"));
        Assertions.assertTrue(indices.get("abc") > indices.get("c"));
        Assertions.assertTrue(indices.get("b") > indices.get("d"));
        Assertions.assertTrue(indices.get("c") > indices.get("d"));
        Assertions.assertTrue(indices.get("d") > indices.get("a"));
        Assertions.assertTrue(indices.get("h") > indices.get("b"));
    }

    private File createFileMock(String path) {
        File mock = Mockito.mock(File.class);
        Mockito.when(mock.getPath()).thenReturn(path);

        return mock;
    }
}
