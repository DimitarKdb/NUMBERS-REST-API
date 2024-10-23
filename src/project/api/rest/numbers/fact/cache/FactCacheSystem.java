package project.api.rest.numbers.fact.cache;

import project.api.rest.numbers.result.Result;
import project.api.rest.numbers.status.Status;
import project.api.rest.numbers.type.facts.FactType;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.Map;
import java.util.TreeMap;

public class FactCacheSystem implements CacheSystem {

    private static final int STRING_BUILDER_CAPACITY = 128;
    private final EnumMap<FactType, Map<String, String>> loadedFacts;
    private final EnumMap<FactType, Map<String, String>> newlyAddedFacts;

    public FactCacheSystem() {
        loadedFacts = new EnumMap<>(FactType.class);
        newlyAddedFacts = new EnumMap<>(FactType.class);

        for (FactType factType : FactType.values()) {

            if (factType == FactType.RANDOM) {
                continue;
            }

            loadedFacts.put(factType, new TreeMap<>());
            newlyAddedFacts.put(factType, new TreeMap<>());
        }
    }

    @Override
    public void loadData(Path file) {

        if (!Files.exists(file)) {
            try {
                Files.createFile(file);
            } catch (IOException e) {
                throw new RuntimeException("An error occurred while trying to create a file!", e);
            }

            return;
        }

        try (BufferedReader rd = Files.newBufferedReader(file)) {

            String line;
            String[] tokens;

            while ((line = rd.readLine()) != null) {
                tokens = line.split(" {2}");
                FactType factType = FactType.valueOf(tokens[0].toUpperCase());

                loadedFacts.get(factType).put(tokens[1], tokens[2]);
            }

        } catch (IOException e) {
            throw new RuntimeException("Could not load facts to cache!", e);
        }

        System.out.println("Data has been loaded to cache!");

    }

    @Override
    public void saveData(Path file) {

        try (var writer = new BufferedWriter(new FileWriter(file.toFile(), true))) {
            StringBuilder sb = new StringBuilder(STRING_BUILDER_CAPACITY);

            for (FactType type : FactType.values()) {
                var newFacts = newlyAddedFacts.get(type);

                if (newFacts == null) {
                    continue;
                }

                for (Map.Entry<String, String> entry : newFacts.entrySet()) {

                    sb.append(type.getType()).append("  ").append(entry.getKey()).append("  ").append(entry.getValue()).append(System.lineSeparator());
                    writer.write(sb.toString());

                    sb.setLength(0);
                }
            }

            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException("Could not cache facts!", e);
        }

        System.out.println("Data has been saved successfully!");
    }

    public Result retrieveFact(String[] parameters, FactType factType) {
        String key;

        if (factType != FactType.DATE) {
            key = parameters[0];
        } else {
            key = parameters[1] + "/" + parameters[0];
        }


        String cachedFact = this.loadedFacts.get(factType).get(key);
        String newFact = this.newlyAddedFacts.get(factType).get(key);

        if (cachedFact != null) {
            return new Result(cachedFact, Status.GOOD, factType);
        } else if (newFact != null) {
            return new Result(newFact, Status.GOOD, factType);
        }

        return null;
    }

    public void loadFact(String value, String key, FactType factType) {

        if (factType == FactType.RANDOM) {
            return;
        }

        if(this.newlyAddedFacts.get(factType).get(key) == null) {
            newlyAddedFacts.get(factType).putIfAbsent(key, value);
        }

    }
}
