# Genetic Algorithm Passcode Finder

This project uses a Genetic Algorithm to crack a randomly generated 32-bit binary passcode. It's built as a small JavaFX app - you set the parameters, hit run, and it evolves a population of random guesses until one matches the target passcode exactly.

## How it works

The passcode is a 32-bit sequence, so each "chromosome" (candidate guess) is just a list of 32 bits (0s and 1s).

- **Initialization** - generate a starting population of totally random bit sequences.
- **Fitness** - for each candidate, count how many bits match the target passcode in the same position. The more matches, the higher the fitness.
- **Selection** - parents are picked using fitness-proportional selection, so candidates closer to the real passcode are more likely to get picked.
- **Crossover** - two parents are combined at a random crossover point to produce two children, each inheriting part of the sequence from each parent.
- **Mutation** - each bit has a small chance of flipping (0 to 1 or 1 to 0), which keeps some randomness in the population so it doesn't get stuck.
- **Replacement & Termination** - the population gets replaced each generation, and the algorithm stops either when a candidate matches the passcode exactly, or when it hits the max number of generations.

Every generation's best fitness gets logged to a `convergence.csv` file so you can track how fast (or slow) the algorithm is converging.

## Running it

1. Java JDK 17+ and JavaFX SDK set up in your project.
2. Run `Main.java`.
3. In the window, set:
   - Population Size (default 100)
   - Mutation Rate (default 0.01)
   - Max Generations (default 1000)
4. Click "Run Algorithm". It'll show whether it found the passcode, the target vs. found passcode, what generation it was found in, and how long it took.

## What we found while testing

We tried a bunch of different parameter combinations and the results varied a lot:

- **Population 100, mutation 1%** - found the passcode in just 84 generations (~88ms). Small population kept things fast, and the low mutation rate let good solutions stick around instead of getting disrupted.
- **Population 175, mutation 3%** - still found it, but took 1046 generations (~422ms). Bigger population meant more diversity but also more computation per generation, and the higher mutation rate slowed down convergence.
- **Population 170, mutation 3%, max 1500 generations** - failed to find it at all. The mutation rate was too disruptive relative to the population size, so good candidates kept getting mutated away before the algorithm could settle.
- **Population 20, mutation 0.01%, max 500 generations** - also failed. Population was too small, so there wasn't enough genetic diversity, and the algorithm converged early on a wrong local solution.

Basically, there's a balance to hit: a population too small or a mutation rate too low leads to premature convergence on the wrong answer, while too high a mutation rate keeps disrupting good progress. Population 100 with a 1% mutation rate gave the best result in our tests.

## Files

- `Main.java` - everything: the GA logic (fitness, selection, crossover, mutation) plus the JavaFX UI
- `GeneticAlgorithmReport.pdf` - full write-up of the approach, parameter tuning attempts, and results

## Group Members

- Monya Assi - 1213503
- Nahed Najjar - 1220704

## References


2. An Introduction to Genetic Algorithms
3. GeeksforGeeks - Genetic Algorithms
4. JavaFX documentation
