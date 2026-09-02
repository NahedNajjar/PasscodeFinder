package application;
	
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main extends Application {

    private static final Random random = new Random();
    static final int PASSCODE_LENGTH = 32;

static String FinalResult=new String();
    public static List<Integer> generateRandomPasscode(int length) {
        List<Integer> passcode = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            passcode.add(random.nextInt(2));
        }
        return passcode;
    }

    public static int fitnessFunction(List<Integer> candidate, List<Integer> target) {
        int fitness = 0;
        for (int i = 0; i < candidate.size(); i++) {
            if (candidate.get(i).equals(target.get(i))) {
                fitness++;
            }
        }
        return fitness;
    }

    public static List<List<Integer>> initializePopulation(int popSize, int chromosomeLength) {
        List<List<Integer>> population = new ArrayList<>();
        for (int i = 0; i < popSize; i++) {
            population.add(generateRandomPasscode(chromosomeLength));
        }
        return population;
    }

    public static List<Integer> selectParent(List<List<Integer>> population, List<Integer> fitnessScores) {
        int totalFitness = fitnessScores.stream().mapToInt(Integer::intValue).sum();
        int randomValue = random.nextInt(totalFitness);
        int cumulativeFitness = 0;

        for (int i = 0; i < population.size(); i++) {
            cumulativeFitness += fitnessScores.get(i);
            if (cumulativeFitness > randomValue) {
                return population.get(i);
            }
        }

        return population.get(0);
    }

    public static List<List<Integer>> crossover(List<Integer> parent1, List<Integer> parent2) {
        int crossoverPoint = random.nextInt(parent1.size() - 1) + 1;
        List<Integer> child1 = new ArrayList<>(parent1.subList(0, crossoverPoint));
        child1.addAll(parent2.subList(crossoverPoint, parent2.size()));
        List<Integer> child2 = new ArrayList<>(parent2.subList(0, crossoverPoint));
        child2.addAll(parent1.subList(crossoverPoint, parent1.size()));

        List<List<Integer>> children = new ArrayList<>();
        children.add(child1);
        children.add(child2);
        return children;
    }

    public static List<Integer> mutate(List<Integer> chromosome, double mutationRate) {
        List<Integer> mutated = new ArrayList<>();
        for (int gene : chromosome) {
            if (random.nextDouble() < mutationRate) {
                mutated.add(1 - gene);
            } else {
                mutated.add(gene);
            }
        }
        return mutated;
    }

    public static int runGeneticAlgorithm(List<Integer> target, int popSize, double mutationRate, int maxGenerations) {
        int chromosomeLength = target.size();
        int bestFitness = 0;
        List<List<Integer>> population = initializePopulation(popSize, chromosomeLength);
        PrintWriter writer = null;
        try {
        	writer = new PrintWriter("convergence.csv");
        	List<String> convergenceData = new ArrayList<>();
            convergenceData.add("Generation,BestFitness");
            writer.println("Generation"+","+"BestFitness"+","+"convergenceRate");
            
        for (int generation = 0; generation < maxGenerations; generation++) {
            List<Integer> fitnessScores = new ArrayList<>();
            for (List<Integer> candidate : population) {
                fitnessScores.add(fitnessFunction(candidate, target));
            }
            bestFitness =getBestFitness(population,target);
            double convergenceRate = (double) bestFitness / target.size();

            writer.println(generation + "," + bestFitness+ "," +convergenceRate);
          //  System.out.println("Generation " + generation + " - Best Fitness: " + bestFitness);
            List<Integer> bestIndividual =
                    getBestIndividual(population, target);

            if (bestFitness == chromosomeLength) {
            	FinalResult="Target Passcode: " +
                        listToString(target)+"\n"+"Found Passcode:  " +
                        listToString(bestIndividual)+"\n"+"Found in generation: " + generation;
                
              
                
            }
            int maxFitness = fitnessScores.stream().mapToInt(Integer::intValue).max().orElse(0);
            if (maxFitness == chromosomeLength) {
                return generation;
            }
         
          //  System.out.println(bestFitness);

            List<List<Integer>> nextGeneration = new ArrayList<>();
            while (nextGeneration.size() < popSize) {
                List<Integer> parent1 = selectParent(population, fitnessScores);
                List<Integer> parent2 = selectParent(population, fitnessScores);
                List<List<Integer>> children = crossover(parent1, parent2);
                nextGeneration.add(mutate(children.get(0), mutationRate));
                nextGeneration.add(mutate(children.get(1), mutationRate));
            }
            population = nextGeneration;
            
        }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                writer.close();
            }}

        return -1; // Indicates failure to find the solution within the limit
    }
    public static int getBestFitness(List<List<Integer>> population, List<Integer> passcode) {
        int best = 0;

        for (List<Integer> individual : population) {
            int fitness = fitnessFunction(individual, passcode);
            if (fitness > best) {
                best = fitness;
            }
        }
        
        return best;
    }
    public static List<Integer> getBestIndividual(
            List<List<Integer>> population,
            List<Integer> target) {

        List<Integer> best = population.get(0);
        int bestFitness = fitnessFunction(best, target);

        for (List<Integer> individual : population) {
            int f = fitnessFunction(individual, target);
            if (f > bestFitness) {
                bestFitness = f;
                best = individual;
            }
        }
        return best;
    }

    public static String listToString(List<Integer> bits) {
        StringBuilder sb = new StringBuilder();
        for (int bit : bits) {
            sb.append(bit);
        }
        return sb.toString();
    }


    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Genetic Algorithm Passcode Finder");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(10);
        grid.setVgap(10);

        Label populationSizeLabel = new Label("Population Size:");
        TextField populationSizeField = new TextField("100");
        Label mutationRateLabel = new Label("Mutation Rate:");
        TextField mutationRateField = new TextField("0.01");
        Label maxGenerationsLabel = new Label("Max Generations:");
        TextField maxGenerationsField = new TextField("1000");

        Button runButton = new Button("Run Algorithm");
        TextArea resultArea = new TextArea();
        resultArea.setEditable(false);

        runButton.setOnAction(e -> {
            try {
                int popSize = Integer.parseInt(populationSizeField.getText());
                double mutationRate = Double.parseDouble(mutationRateField.getText());
                int maxGenerations = Integer.parseInt(maxGenerationsField.getText());

                List<Integer> passcode = generateRandomPasscode(32);
                
                long startTime = System.nanoTime();

                int generations = runGeneticAlgorithm(passcode, popSize, mutationRate, maxGenerations);
                long endTime = System.nanoTime();
                long executionTimeMs = (endTime - startTime) / 1_000_000;

               // System.out.println("Time taken: " + executionTimeMs + " ms");

                if (generations != -1) {
                    resultArea.setText("Passcode found !"+"\n");
                    resultArea.appendText(FinalResult+"\n");
                    resultArea.appendText("Time taken: " + executionTimeMs + " ms");
                    
                } else {
                    resultArea.setText("Failed to find the passcode within the maximum generations.");
                //    resultArea.appendText("\nGeneration " + gen + " - Best Fitness: " + bestFitness);

                }
            } catch (NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Please enter valid numeric values for all inputs.");
                alert.showAndWait();
            }
        });

        grid.add(populationSizeLabel, 0, 0);
        grid.add(populationSizeField, 1, 0);
        grid.add(mutationRateLabel, 0, 1);
        grid.add(mutationRateField, 1, 1);
        grid.add(maxGenerationsLabel, 0, 2);
        grid.add(maxGenerationsField, 1, 2);
        grid.add(runButton, 0, 3, 2, 1);
        grid.add(resultArea, 0, 4, 2, 1);

        Scene scene = new Scene(grid, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
