import java.util.Random;
import java.util.List;

/**
 * Common elements of foxes and rabbits.
 *
 * @author David J. Barnes and Michael Kölling
 * @version 7.0
 */
public abstract class Animal
{
    // Whether the animal is alive or not.
    private boolean alive;
    // The animal's position.
    private Location location;
    // The value will reset in subclass.
    // The age at which a fox can start to breed.
    protected int BREEDING_AGE;
    // The age to which a fox can live.
    protected int maxAge;
    // The likelihood of a fox breeding.
    protected double breedingProbability;
    // The maximum number of births.
    protected int maxLitterSize;
    // The age of the animal
    protected int age;
    // The gender of the animal
    protected String gender;
    
    // A shared random number generator to control breeding.
    protected static final Random rand = Randomizer.getRandom();

    /**
     * Constructor for objects of class Animal.
     * @param location The animal's location.
     */
    public Animal(Location location)
    {
        this.alive = true;
        this.location = location;
        this.age = 0;
        this.gender = assignGender();
    }
    
    /**
     * Act.
     * @param currentField The current state of the field.
     * @param nextFieldState The new state being built.
     */
    abstract public void act(Field currentField, Field nextFieldState);
    
    /**
     * Increase the age.
     * This could result in the animal's death.
     */
    protected void incrementAge()
    {
        age++;
        if(age > maxAge) {
            setDead();
        }
    }
    
    /**
     * This method assign a random gender to an animal
     * @return the gender of animal
     */
    protected String assignGender()
    {
        Random random = new Random();
        if (random.nextBoolean()) {
            return "male";
        }
        else {
            return "female";
        }
    }
    
    /**
     * Check whether the animal is alive or not.
     * @return true if the animal is still alive.
     */
    public boolean isAlive()
    {
        return alive;
    }

    /**
     * Indicate that the animal is no longer alive.
     */
    protected void setDead()
    {
        alive = false;
        location = null;
    }
    
    /**
     * Return the animal's location.
     * @return The animal's location.
     */
    public Location getLocation()
    {
        return location;
    }
    
    /**
     * Set the animal's location.
     * @param location The new location.
     */
    protected void setLocation(Location location)
    {
        this.location = location;
    }
    
    /**
     * 
     */
    protected void giveBirth(Field nextFieldState, List<Location> freeLocations, Field currentField) {
        int births = breed(currentField);
        if (births > 0) {
            for (int b = 0; b < births && !freeLocations.isEmpty(); b++) {
                Location loc = freeLocations.remove(0);
                Animal young = createYoung(false, loc);
                nextFieldState.placeAnimal(young, loc);
            }
        }
    }

    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    protected int breed(Field currentField)
    {
        int births;
        if(canBreed(currentField) && rand.nextDouble() <= breedingProbability) {
            births = rand.nextInt(maxLitterSize) + 1;
        }
        else {
            births = 0;
        }
        return births;
    }

    /**
     * A rabbit can breed if it has reached the breeding age.
     * @return true if the rabbit can breed, false otherwise.
     */
    protected boolean canBreed(Field currentField)
    {
        if (age >= BREEDING_AGE) {
            List<Location> adjacentLocations = 
                currentField.getFreeAdjacentLocations(getLocation());
                for (Location loc : adjacentLocations) {
                    Animal animal = currentField.getAnimalAt(loc);
                    if (animal != null && animal.getClass() == this.getClass() && !animal.gender.equals(this.gender) && animal.isAlive()) {
                        return true;
                    }
                }
        }
        return false;
    }
    
    /**
     * 
     */
    protected abstract Animal createYoung(boolean randomAge, Location location);
}
