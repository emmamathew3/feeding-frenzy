import tester.*;
import java.util.Random;
import javalib.worldimages.*;
import javalib.funworld.*;
import java.awt.Color;

//Utils class for certain operations
class Utils {
  
  //checks if two ranges are within each other
  boolean checkTwoRanges(double lower1, double upper1, double lower2, double upper2) {
    return lower1 >= lower2 && lower1 <= upper2
        || upper1 >= lower2 && upper1 <= upper2
        || lower2 >= lower1 && lower2 <= upper1
        || upper2 >= lower1 && upper2 <= upper1;
  }
}

//represents a world class to animate fish on a screen
class FishWorld extends World {
  AFish player;
  ILoFish bgFish;
  
  //constructs a fishworld with a player and background fish
  FishWorld(AFish player, ILoFish bgFish) {
    this.player = player;
    this.bgFish = bgFish;
  }
  
  //constructs a default fishworld
  FishWorld() {
    ILoFish bgFish = new MtLoFish().addLoFish(4); 
    AFish player = bgFish.safePlayer();
    this.player = player;
    this.bgFish = bgFish;
  }
  
  /*
   * fields:
   * this.player ... AFish
   * this.bgFish ... ILoFish
   * 
   * methods:
   * this.makeScene() ... WorldScene
   * this.lastScene(String) ... WorldScene
   * this.onTick() ... World
   * this.onKeyEvent(String) ... World
   * this.fishOverlapping() ... ILoFish
   * this.whichGameOver(ILoFish) ... String
   * this.checkEaten() ... World
   * 
   * methods of fields:
   * this.player.draw(WorldScene) ... WorldScene
   * this.player.drawShape() ... WorldImage
   * this.player.overlapFish(AFish) ... boolean
   * this.player.move() ... AFish
   * this.player.onKey(String) ... AFish
   * this.player.isSameFish(AFish) ... boolean
   * this.player.updateSize(ILoFish) ... AFish
   * this.player.biggerThan(AFish) ... boolean
   * this.player.addSize(AFish) ... AFish
   * this.player.tooBig() ... boolean
   * 
   * this.bgFish.draw(WorldScene) ... WorldScene
   * this.bgFish.addLoFish(int) ... ILoFish
   * this.bgFish.addLoFishForTesting(int) ... ILoFish
   * this.bgFish.overlapFishInList(AFish) ... boolean
   * this.bgFish.addFish() ... ILoFish
   * this.bgFish.addFishForTesting() ... ILoFish
   * this.bgFish.move() ... ILoFish
   * this.bgFish.fishOverlapping() ... ILoFish
   * this.bgFish.removeFish(ILoFish) ... ILoFish
   * this.bgFish.contains(AFish) ... boolean
   * this.bgFish.atePlayer(AFish) ... boolean
   * this.bgFish.updateSize(AFish) ... AFish
   * this.bgFish.safePlayer() ... AFish
   * 
   * 
   */
  
  //Draws the world state
  //(both the player and the background fish on the scene)
  public WorldScene makeScene() {
    return this.player.draw(this.bgFish.draw(new WorldScene(400, 800)));
  }
  
  //Game over scene
  public WorldScene lastScene(String msg) {
    if (msg.equals("You lose!!")) {
      return new WorldScene(400, 800).placeImageXY(
          new RectangleImage(400, 800, OutlineMode.SOLID, Color.DARK_GRAY), 200, 400).placeImageXY(
              new TextImage(msg, 30, Color.ORANGE), 200, 400); 
    }
    else {
      return new WorldScene(400, 800).placeImageXY(
          new RectangleImage(400, 800, OutlineMode.SOLID, Color.cyan), 200, 400).placeImageXY(
              new TextImage("YOU WON!!!", 30, Color.ORANGE), 200, 400);
    }
  }
  
  
  //Moves the fish at each tick and adds a fish to the world 1/30 of the time
  public World onTick() {
    Random r = new Random();
    //move all fish
    ILoFish move = this.bgFish.move();
    //make a new world
    FishWorld newWorld = new FishWorld(this.player, move);
    ILoFish overlap = newWorld.fishOverlapping();
    String gameOver = newWorld.whichGameOver(overlap);
    
    if (gameOver.equals("You lose!!") || gameOver.equals("You win!!")) {
      return this.endOfWorld(gameOver);
    } 
    else {
      if (r.nextInt(30) == 0) {
        return (new FishWorld(this.player, move.addFish())).checkEaten();
      }
      else {
        return (new FishWorld(this.player, move)).checkEaten();
      }
    }
  }
  
  //ontick for testing
  public World onTickForTesting() {
    ILoFish move = this.bgFish.move();
    //make a new world
    FishWorld newWorld = new FishWorld(this.player, move);
    ILoFish overlap = newWorld.fishOverlapping();
    String gameOver = newWorld.whichGameOver(overlap);
    
    if (gameOver.equals("You lose!!") || gameOver.equals("You win!!")) {
      return this.endOfWorld(gameOver);
    } 
    else {
      return (new FishWorld(this.player, move.addLoFishForTesting(1))).checkEaten();
    }
  }
  
  //add a key event to move the player fish around
  //checks whether the fish has eaten a background fish
  public World onKeyEvent(String key) {
    if (key.equals("up") || key.equals("down") || key.equals("left") || key.equals("right")) {
      FishWorld newWorld = new FishWorld(this.player.onKey(key), this.bgFish);

      ILoFish overlap = newWorld.fishOverlapping();
      String gameOver = newWorld.whichGameOver(overlap);
      if (gameOver.equals("You lose!!") || gameOver.equals("You win!!")) {
        return this.endOfWorld(gameOver);
      } 
      else {
        return newWorld.checkEaten();
      }
    }
    else {
      return this;
    }
  }
   
  // returns a list of the fish that overlap the player fish
  public ILoFish fishOverlapping() {
    
    if (this.bgFish.overlapFishInList(this.player)) {
      return this.bgFish.fishOverlapping(this.player);
    }
    return new MtLoFish();
  }

  // returns which game is over
  public String whichGameOver(ILoFish overlap) {
    /*
     * methods
     * overlap.draw(WorldScene) ... WorldScene
     * overlap.addLoFish(int) ... ILoFish
     * overlap.addLoFishForTesting(int) ... ILoFish
     * overlap.overlapFishInList(AFish) ... boolean
     * overlap.addFish() ... ILoFish
     * overlap.addFishForTesting() ... ILoFish
     * overlap.move() ... ILoFish
     * overlap.fishOverlapping() ... ILoFish
     * overlap.removeFish(ILoFish) ... ILoFish
     * overlap.contains(AFish) ... boolean
     * overlap.atePlayer(AFish) ... boolean
     * overlap.updateSize(AFish) ... AFish
     * overlap.safePlayer() ... AFish
     */
    if (overlap.atePlayer(this.player)) {
      return "You lose!!";
    }
    else if (this.player.tooBig()) {
      return "You win!!";
    }
    else {
      return "";
    }
  }

  // Returns a updated fishworld when the player fish eats another fish
  public World checkEaten() {
    ILoFish fishOverlapping = this.fishOverlapping();

    // updates the size of the player when it eats a fish
    this.player = this.player.updateSize(fishOverlapping);

    // removes the fish that overlap
    this.bgFish = this.bgFish.removeFish(fishOverlapping);

    return new FishWorld(this.player, this.bgFish);
  }

}

//Represents a list of background fish
interface ILoFish {
  
  //draws the list of fish onto the WorldScene acc
  public WorldScene draw(WorldScene acc);

  //generates a list of fish of the given number
  public ILoFish addLoFish(int num);
  
  //generates a list of fish of the given number for testing
  public ILoFish addLoFishForTesting(int num);
  
  //checks whether a fish overlaps any fish in this list
  public boolean overlapFishInList(AFish a);
  
  //adds 1 fish to the list of background fish
  public ILoFish addFish();
  
  //adds 1 fish to the list of background fish for testing
  public ILoFish addFishForTesting();
  
  //moves all the background fish by their speed
  public ILoFish move();
  
  //Returns the fish that overlap with the player fish
  public ILoFish fishOverlapping(AFish player);
  
  //removes the fish that are in fishList from this list
  public ILoFish removeFish(ILoFish fishList);

  //returns whether the fish first is in this list
  public boolean contains(AFish first);
  
  //Returns whether the player has been eaten
  //aka if it collided with a bigger fish
  public boolean atePlayer(AFish player);

  //updates the size of the fish once it eats another fish
  public AFish updateSize(AFish aFish);
  
  //returns a player that does not overlap with any fish
  public AFish safePlayer();
}

//Represents a list of empty fish
class MtLoFish implements ILoFish {

  /*
   * methods
   * this.draw(WorldScene) ... WorldScene
   * this.addLoFish(int) ... ILoFish
   * this.addLoFishForTesting(int) ... ILoFish
   * this.overlapFishInList(AFish) ... boolean
   * this.addFish() ... ILoFish
   * this.addFishForTesting() ... ILoFish
   * this.move() ... ILoFish
   * this.fishOverlapping() ... ILoFish
   * this.removeFish(ILoFish) ... ILoFish
   * this.contains(AFish) ... boolean
   * this.atePlayer(AFish) ... boolean
   * this.updateSize(AFish) ... AFish
   * this.safePlayer() ... AFish
   */
  
  //draws the list of fish on the acc background
  public WorldScene draw(WorldScene acc) {
    return acc;
  }

  //generates a list of fish of the given number
  public ILoFish addLoFish(int num) {
    if (num == 0) {
      return this;
    }
    else {
      return this.addFish().addLoFish(num - 1);
    }
  }
  
  //generates a list of fish of the given number for testing
  public ILoFish addLoFishForTesting(int num) {
    if (num == 0) {
      return this;
    }
    else {
      Random r = new Random(20);
      return new ConsLoFish(new BGFish(r), new MtLoFish().addLoFishForTesting(num - 1)); 
    }
  }

  //checks whether a fish overlaps any fish in this list
  public boolean overlapFishInList(AFish a) {
    /* methods
    * a.draw(WorldScene) ... WorldScene
    * a.drawShape() ... WorldImage
    * a.overlapFish(AFish) ... boolean
    * a.move() ... AFish
    * a.onKey(String) ... AFish
    * a.isSameFish(AFish) ... boolean
    * a.updateSize(ILoFish) ... AFish
    * a.biggerThan(AFish) ... boolean
    * a.addSize(AFish) ... AFish
    * a.tooBig() ... boolean
    */
    return false;
  }

  //adds 1 fish to the list of background fish
  public ILoFish addFish() {
    return new ConsLoFish(new BGFish(), this);
  }

  //adds 1 fish to the list of background fish for testing 
  public ILoFish addFishForTesting() {
    Random r = new Random(20);
    return new ConsLoFish(new BGFish(r), this);
  }

  //moves all the background fish by their speed
  public ILoFish move() {
    return this;
  }

  //Returns the fish that overlap with the player fish
  public ILoFish fishOverlapping(AFish player) {
    return this;
  }

  //Removes the fish that are in fishList from this list
  public ILoFish removeFish(ILoFish fishList) {
    /*
     * methods
     * fishList.draw(WorldScene) ... WorldScene
     * fishList.addLoFish(int) ... ILoFish
     * fishList.addLoFishForTesting(int) ... ILoFish
     * fishList.overlapFishInList(AFish) ... boolean
     * fishList.addFish() ... ILoFish
     * fishList.addFishForTesting() ... ILoFish
     * fishList.move() ... ILoFish
     * fishList.fishOverlapping() ... ILoFish
     * fishList.removeFish(ILoFish) ... ILoFish
     * fishList.contains(AFish) ... boolean
     * fishList.atePlayer(AFish) ... boolean
     * fishList.updateSize(AFish) ... AFish
     * fishList.safePlayer() ... AFish
     */
    return this;
  }

  //returns whether the fish first is in this list
  public boolean contains(AFish first) {
    return false;
  }

  //returns whether the player fish has been eaten
  public boolean atePlayer(AFish player) {
    return false;
  }

  //updates the size of the fish once it eats another fish
  public AFish updateSize(AFish aFish) {
    return aFish;
  }
  
  //returns a player that does not overlap any fish in the list
  public AFish safePlayer() {
    return new PlayerFish();
  }
}

//Represents a non-empty list of background fish
class ConsLoFish implements ILoFish {
  AFish first;
  ILoFish rest;
  
  //Generates a non-empty list of fish
  ConsLoFish(AFish first, ILoFish rest) {
    this.first = first;
    this.rest = rest;
  }
  
  /*
   * fields
   * this.first ... AFish
   * this.rest ... ILoFish
   * 
   * methods
   * this.draw(WorldScene) ... WorldScene
   * this.addLoFish(int) ... ILoFish
   * this.addLoFishForTesting(int) ... ILoFish
   * this.overlapFishInList(AFish) ... boolean
   * this.addFish() ... ILoFish
   * this.addFishForTesting() ... ILoFish
   * this.move() ... ILoFish
   * this.fishOverlapping() ... ILoFish
   * this.removeFish(ILoFish) ... ILoFish
   * this.contains(AFish) ... boolean
   * this.atePlayer(AFish) ... boolean
   * this.updateSize(AFish) ... AFish
   * this.safePlayer() ... AFish
   * 
   * methods of fields
   * this.first.draw(WorldScene) ... WorldScene
   * this.first.drawShape() ... WorldImage
   * this.first.overlapFish(AFish) ... boolean
   * this.first.move() ... AFish
   * this.first.onKey(String) ... AFish
   * this.first.isSameFish(AFish) ... boolean
   * this.updateSize(ILoFish) ... AFish
   * this.first.biggerThan(AFish) ... boolean
   * this.first.addSize(AFish) ... AFish
   * this.first.tooBig() ... boolean
   * 
   * this.rest.draw(WorldScene) ... WorldScene
   * this.rest.addLoFish(int) ... ILoFish
   * this.rest.addLoFishForTesting(int) ... ILoFish
   * this.rest.overlapFishInList(AFish) ... boolean
   * this.rest.addFish() ... ILoFish
   * this.rest.addFishForTesting() ... ILoFish
   * this.rest.move() ... ILoFish
   * this.rest.fishOverlapping() ... ILoFish
   * this.rest.removeFish(ILoFish) ... ILoFish
   * this.rest.contains(AFish) ... boolean
   * this.rest.atePlayer(AFish) ... boolean
   * this.rest.updateSize(AFish) ... AFish
   * this.rest.safePlayer() ... AFish
   */

  //draws the fish on the acc background
  public WorldScene draw(WorldScene acc) {
    /*
    * this.makeScene() ... WorldScene
    * this.lastScene(String) ... WorldScene
    * this.onTick() ... World
    * this.onKeyEvent(String) ... World
    * this.fishOverlapping() ... ILoFish
    * this.whichGameOver(ILoFish) ... String
    * this.checkEaten() ... World
    */
    return this.rest.draw(this.first.draw(acc));
  }

  //generates a list of fish of the given number
  public ILoFish addLoFish(int num) {
    if (num == 0) {
      return this;
    }
    else {
      return this.addFish().addLoFish(num - 1);
    }
  }
  
  //generates a list of fish of the given number
  public ILoFish addLoFishForTesting(int num) {
    return new ConsLoFish(this.first, this.rest.addLoFishForTesting(num));
  }

  //checks whether a fish overlaps any fish in this list
  public boolean overlapFishInList(AFish a) {
    /* methods
    * a.draw(WorldScene) ... WorldScene
    * a.drawShape() ... WorldImage
    * a.overlapFish(AFish) ... boolean
    * a.move() ... AFish
    * a.onKey(String) ... AFish
    * a.isSameFish(AFish) ... boolean
    * a.updateSize(ILoFish) ... AFish
    * a.biggerThan(AFish) ... boolean
    * a.addSize(AFish) ... AFish
    * a.tooBig() ... boolean
    */
    return this.first.overlapFish(a) || this.rest.overlapFishInList(a);
  }

  //adds 1 fish to the list of background fish
  //if it does not overlap
  public ILoFish addFish() {
    AFish f = new BGFish();
    if (this.overlapFishInList(f)) {
      return this;
    }
    else {
      return new ConsLoFish(f, this);
    }
  }

  //adds 1 fish to the list of background fish
  public ILoFish addFishForTesting() {
    AFish f = new BGFish(new Random(20));
    if (this.overlapFishInList(f)) {
      return this.addFishForTesting();
    }
    else {
      return new ConsLoFish(f, this);
    }
  }

  //moves all the background fish by their speed
  public ILoFish move() {
    return new ConsLoFish(this.first.move(), this.rest.move());
  }

  //Returns the fish that overlap with the player fish
  public ILoFish fishOverlapping(AFish player) {
    
    /* methods
    * player.draw(WorldScene) ... WorldScene
    * player.drawShape() ... WorldImage
    * player.overlapFish(AFish) ... boolean
    * player.move() ... AFish
    * player.onKey(String) ... AFish
    * player.isSameFish(AFish) ... boolean
    * player.updateSize(ILoFish) ... AFish
    * player.biggerThan(AFish) ... boolean
    * player.addSize(AFish) ... AFish
    * player.tooBig() ... boolean
    */
    if (this.first.overlapFish(player)) {
      return new ConsLoFish(this.first, this.rest.fishOverlapping(player));
    }
    else {
      return this.rest.fishOverlapping(player);
    }
  }

  //Removes the fish that are in fishList from this list
  public ILoFish removeFish(ILoFish fishList) {
    /*
     * methods
     * fishList.draw(WorldScene) ... WorldScene
     * fishList.addLoFish(int) ... ILoFish
     * fishList.addLoFishForTesting(int) ... ILoFish
     * fishList.overlapFishInList(AFish) ... boolean
     * fishList.addFish() ... ILoFish
     * fishList.addFishForTesting() ... ILoFish
     * fishList.move() ... ILoFish
     * fishList.fishOverlapping() ... ILoFish
     * fishList.removeFish(ILoFish) ... ILoFish
     * fishList.contains(AFish) ... boolean
     * fishList.atePlayer(AFish) ... boolean
     * fishList.updateSize(AFish) ... AFish
     * fishList.safePlayer() ... AFish
     */
    if (fishList.contains(this.first)) {
      return this.rest.removeFish(fishList);
    }
    else {
      return new ConsLoFish(this.first, this.rest.removeFish(fishList));
    }
  }

  //returns whether the fish first is in this list
  public boolean contains(AFish fish) {
    /* methods
     * fish.draw(WorldScene) ... WorldScene
     * fish.drawShape() ... WorldImage
     * fish.overlapFish(AFish) ... boolean
     * fish.move() ... AFish
     * fish.onKey(String) ... AFish
     * fish.isSameFish(AFish) ... boolean
     * fish.updateSize(ILoFish) ... AFish
     * fish.biggerThan(AFish) ... boolean
     * fish.addSize(AFish) ... AFish
     * fish.tooBig() ... boolean
     */
    return this.first.isSameFish(fish) || this.rest.contains(fish);
  }

  //returns whether the player fish has been eaten
  public boolean atePlayer(AFish player) {
    /* methods
     * player.draw(WorldScene) ... WorldScene
     * player.drawShape() ... WorldImage
     * player.overlapFish(AFish) ... boolean
     * player.move() ... AFish
     * player.onKey(String) ... AFish
     * player.isSameFish(AFish) ... boolean
     * player.updateSize(ILoFish) ... AFish
     * player.biggerThan(AFish) ... boolean
     * player.addSize(AFish) ... AFish
     * player.tooBig() ... boolean
     */
    return this.first.biggerThan(player) || this.rest.atePlayer(player);
  }

  //updates the size of the fish once it eats another fish
  public AFish updateSize(AFish aFish) {
    return this.rest.updateSize(aFish.addSize(this.first));
  }
  
  //returns a player that does not overlap any fish
  //in this list
  public AFish safePlayer() {
    AFish player = new PlayerFish();
    if (this.overlapFishInList(player)) {
      return new PlayerFish();
    }
    else {
      return player;
    }
  }
}

//Represents either a player fish or a background fish
abstract class AFish {
  double size;
  CartPt location;
  Color color;
  Random rand;
  Boundary bound;
  int speed;
  
  //constructs a fish with a specified size and specified location,
  //the default color blue, a new random number, a boundary
  //generates a random speed either left or right between -15 to -5 and 5 to 15
  AFish(double size, CartPt location) {
    this.size = size;
    this.location = location;
    this.color = Color.BLUE;
    this.rand = new Random();
    this.bound = new Boundary(location, this.drawShape().getWidth(), this.drawShape().getHeight());
    //generates a random speed either left or right between -15 to -5 and 5 to 15
    if (this.rand.nextInt(2) == 0) {
      this.speed = new Random().nextInt(10) - 15; 
    }
    else {
      this.speed = new Random().nextInt(10) + 5; 
    }
  }
  

  //constructs a fish with a specified size, specified location, 
  //specified color, a new random number, a boundary, a speed of 15
  //Used for playerfish construction
  AFish(double size, CartPt location, Color color) {
    this(size, location);
    this.color = color;
    this.rand = new Random();
    this.bound = new Boundary(location, this.drawShape().getWidth(), this.drawShape().getHeight());
    this.speed = 15;
  }
  
  //constructs a fish with a specified size, specified location, 
  //specified color, a specified random number, a boundary, and a random specified 
  // speed either left or right between -15 to -5 and 5 to 15
  AFish(double size, CartPt location, Color color, Random r) {
    this(size, location);
    this.color = color;
    this.rand = r;
    this.bound = new Boundary(location, this.drawShape().getWidth(), this.drawShape().getHeight());
    if (this.rand.nextInt(1) == 0) {
      this.speed = new Random().nextInt(10) - 15; 
    }
    else {
      this.speed = new Random().nextInt(10) + 5; 
    }
  }
  
  //constructs a fish with a specified size, specified location, 
  // default color blue, a specified random number, a boundary, and a random specified 
  //speed between -5 and 5
  AFish(double size, CartPt location, Random r) {
    this(size, location);
    this.rand = r;
    this.bound = new Boundary(location, this.drawShape().getWidth(), this.drawShape().getHeight());
    if (this.rand.nextInt(1) == 0) {
      this.speed = new Random().nextInt(10) - 15; 
    }
    else {
      this.speed = new Random().nextInt(10) + 5; 
    }
  }
  
  /*
   * fields
   * this.size ... double
   * this.location .. CartPt
   * this.color ... Color
   * this.rand ... Random
   * this.bound ... Boundary
   * this.speed ... int
   * 
   * methods
   * this.draw(WorldScene) ... WorldScene
   * this.drawShape() ... WorldImage
   * this.overlapFish(AFish) ... boolean
   * this.move() ... AFish
   * this.onKey(String) ... AFish
   * this.isSameFish(AFish) ... boolean
   * this.updateSize(ILoFish) ... AFish
   * this.biggerThan(AFish) ... boolean
   * this.addSize(AFish) ... AFish
   * this.tooBig() ... boolean
   * 
   * methods of fields
   * this.bound.overlappingBoundary(Boundary) ... boolean
   * this.bound.isSameBoundary(Boundary) ... boolean
   * this.location.drawShapeOnScene(WorldScene, WorldImage) ... WorldScene
   * this.location.getX() ... int
   * this.location.getY() ... int
   * this.location.move(int) ... CartPt
   * this.location.onKey(String, int) ... CartPt
   * this.location.isSameCartPt(CartPt) ... boolean
   */
  
  //draw this Fish onto the world scene
  public WorldScene draw(WorldScene acc) {
    /*
    * this.makeScene() ... WorldScene
    * this.lastScene(String) ... WorldScene
    * this.onTick() ... World
    * this.onKeyEvent(String) ... World
    * this.fishOverlapping() ... ILoFish
    * this.whichGameOver(ILoFish) ... String
    * this.checkEaten() ... World
    */
    return this.location.drawShapeOnScene(acc, this.drawShape());
  }

  //draw this fish as an ellipse and triangle with color
  public WorldImage drawShape() {
    WorldImage shape = new ScaleImage(new OverlayOffsetImage(new EllipseImage(60, 30, 
        OutlineMode.SOLID, this.color),
        -20, 0, new RotateImage(new EquilateralTriangleImage(40, OutlineMode.SOLID, 
            this.color), 90)), this.size);
    if (this.speed < 0) {
      return new RotateImage(shape, 180);
    }
    else {
      return shape;
    }
  }
  
  //checks whether this fish overlaps the given fish
  public boolean overlapFish(AFish that) {
    return this.bound.overlappingBoundary(that.bound);
  }
  
  abstract AFish move();
  
  //add a key event to move the player fish around
  public AFish onKey(String key) {
    return this;
  }
  
  //Returns whether this fish is the same as the given fish
  public boolean isSameFish(AFish that) {
    return this.size == that.size
      && this.location.isSameCartPt(that.location)
      && this.color.equals(that.color)
      && this.bound.isSameBoundary(that.bound);
  }

  //updates the size of the BGFish 
  public AFish updateSize(ILoFish fishOverlapping) {
    /*
     * methods
     * fishOverlapping.draw(WorldScene) ... WorldScene
     * fishOverlapping.addLoFish(int) ... ILoFish
     * fishOverlapping.addLoFishForTesting(int) ... ILoFish
     * fishOverlapping.overlapFishInList(AFish) ... boolean
     * fishOverlapping.addFish() ... ILoFish
     * fishOverlapping.addFishForTesting() ... ILoFish
     * fishOverlapping.move() ... ILoFish
     * fishOverlapping.fishOverlapping() ... ILoFish
     * fishOverlapping.removeFish(ILoFish) ... ILoFish
     * fishOverlapping.contains(AFish) ... boolean
     * fishOverlapping.atePlayer(AFish) ... boolean
     * fishOverlapping.updateSize(AFish) ... AFish
     * fishOverlapping.safePlayer() ... AFish
     */
    return fishOverlapping.updateSize(this);
  }
  
  //Returns whether this fish is bigger than that fish 
  //for eating purposes
  public boolean biggerThan(AFish that) {
    return this.size > that.size;
  }
  
  //adds the size of that fish to this
  abstract AFish addSize(AFish that);
  
  //returns whether the fish is bigger than 2.5
  public boolean tooBig() {
    return this.size > 2.5;
  }
}

//Represents a player fish
class PlayerFish extends AFish {
  
  //constructs a PlayerFish with a specified size, specified location,
  //color orange, and new random number
  PlayerFish(double size, CartPt location) {
    super(size, location, Color.ORANGE);
  }
  
  //constructs a PlayerFish with a size of 1, random location,
  //color orange, and a new random number
  PlayerFish() {
    super(1, new CartPt(), Color.ORANGE);
  }
  
  //constructs a PlayerFish with a size of 1, random location,
  // color orange, and specified random number
  PlayerFish(Random r) {
    super(1, new CartPt(), Color.ORANGE, r);
  }
  
  //constructs a PlayerFish with a specified size, specified location,
  //color orange, and new random number
  PlayerFish(double size, CartPt location, Random r) {
    super(size, location, Color.ORANGE, r);
  }
  
  /*
   * fields
   * this.size ... double
   * this.location .. CartPt
   * this.color ... Color
   * this.rand ... Random
   * this.bound ... Boundary
   * 
   * methods
   * this.draw(WorldScene) ... WorldScene
   * this.drawShape() ... WorldImage
   * this.overlapFish(AFish) ... boolean
   * this.move() ... AFish
   * this.onKey(String) ... AFish
   * this.isSameFish(AFish) ... boolean
   * this.updateSize(ILoFish) ... AFish
   * this.biggerThan(AFish) ... boolean
   * this.addSize(AFish) ... AFish
   * this.tooBig() ... boolean
   * 
   * methods of fields
   * this.bound.overlappingBoundary(Boundary) ... boolean
   * this.bound.isSameBoundary(Boundary) ... boolean
   * this.location.drawShapeOnScene(WorldScene, WorldImage) ... WorldScene
   * this.location.getX() ... int
   * this.location.getY() ... int
   * this.location.move(int) ... CartPt
   * this.location.onKey(String, int) ... CartPt
   * this.location.isSameCartPt(CartPt) ... boolean
   */
  
  //moves a bgfish by its speed
  public AFish move() {
    return new PlayerFish(this.size, this.location.move(speed));
  }
  
  //add a key event to move the player fish around
  public AFish onKey(String key) {
    if (key.equals("up") || key.equals("down") || key.equals("left") || key.equals("right")) {
      return new PlayerFish(this.size, this.location.onKey(key, this.speed));
    }
    else {
      return this;
    }
  }

  //adds the size of that fish to this
  AFish addSize(AFish that) {
    return new PlayerFish(this.size + that.size / 5, this.location);
  }
}

//Represents a background fish
class BGFish extends AFish {
  //constructs a BGFish with a random size from 0.25 to 2.5, a random location,
  //the default color blue, a new random number, and a random speed between -5 and 5
  BGFish() {
    //generates a fish with a random size from 0.25 to 2.5
    super((new Random().nextDouble() * 2.25) + 0.25, new CartPt());
  }
  
  //constructs a BGFish with a random size from 0.25 to 2.5, a specified random location,
  //the default color blue, a specified random number, and a specified random speed between -5 and 5
  BGFish(Random r) {
    super((r.nextDouble() * 2.25) + 0.25, new CartPt(r), r);
    this.speed = r.nextInt(10) - 5;
  }
  
  //draws a fish of a specific size, specific location, 
  //the default color blue, a specified random number, and a specified speed
  BGFish(double size, CartPt location, Random r, int speed) {
    super(size, location, r);
    this.speed = speed;
  }
  
  /*
   * fields
   * this.size ... double
   * this.location .. CartPt
   * this.color ... Color
   * this.rand ... Random
   * this.bound ... Boundary
   * this.speed ... int
   * 
   * methods
   * this.draw(WorldScene) ... WorldScene
   * this.drawShape() ... WorldImage
   * this.overlapFish(AFish) ... boolean
   * this.move() ... AFish
   * this.onKey(String) ... AFish
   * this.isSameFish(AFish) ... boolean
   * this.updateSize(ILoFish) ... AFish
   * this.biggerThan(AFish) ... boolean
   * this.addSize(AFish) ... AFish
   * this.tooBig() ... boolean
   * 
   * methods of fields
   * this.bound.overlappingBoundary(Boundary) ... boolean
   * this.bound.isSameBoundary(Boundary) ... boolean
   * this.location.drawShapeOnScene(WorldScene, WorldImage) ... WorldScene
   * this.location.getX() ... int
   * this.location.getY() ... int
   * this.location.move(int) ... CartPt
   * this.location.onKey(String, int) ... CartPt
   * this.location.isSameCartPt(CartPt) ... boolean
   */
  
  //moves the BGFish by its speed
  public AFish move() {
    return new BGFish(this.size, this.location.move(speed), this.rand, this.speed);
  }
  
  //adds the size of that fish to this
  AFish addSize(AFish that) {
    return new BGFish(this.size + that.size / 5, this.location, this.rand, this.speed);
  }
  
}

//represents a location 
class CartPt {
  int x;
  int y;
  
  //generates a CartPt at a specific x and specific y
  CartPt(int x, int y) {
    this.x = x;
    this.y = y;
  }

  //generates a CartPt at a random x and random y
  CartPt() {
    Random r = new Random();
    this.x = r.nextInt(400);
    this.y = r.nextInt(800);
  }
  
  //generates a CartPt at a specific random x and specific random y
  CartPt(Random r) {
    this.x = r.nextInt(400);
    this.y = r.nextInt(800);
  }
  
  /*
   * fields
   * this.x ... int
   * this.y ... int
   * 
   * methods
   * this.drawShapeOnScene(WorldScene, WorldImage) ... WorldScene
   * this.getX() ... int
   * this.getY() ... int
   * this.move(int) ... CartPt
   * this.onKey(String, int) ... CartPt
   * this.isSameCartPt(CartPt) ... boolean
   */
  
  //Draws a shape on the scene at this x and y location
  WorldScene drawShapeOnScene(WorldScene acc, WorldImage shape) {
    /*
    * this.makeScene() ... WorldScene
    * this.lastScene(String) ... WorldScene
    * this.onTick() ... World
    * this.onKeyEvent(String) ... World
    * this.fishOverlapping() ... ILoFish
    * this.whichGameOver(ILoFish) ... String
    * this.checkEaten() ... World
    */
    return acc.placeImageXY(shape, this.x, this.y);
  }
  
  //Returns the x coordinate of CartPt
  int getX() {
    return this.x;
  }
  
  //Returns the y coordinate of CartPt
  int getY() {
    return this.y;
  }
  
  //moves a location by the speed
  CartPt move(int speed) {
    if (speed < 0 && this.x + speed <= 0) {
      return new CartPt(400, this.y);
    }
    
    if (speed > 0 && this.x + speed >= 400) {
      return new CartPt(0, this.y);
    }
    return new CartPt((x + speed), y);
  }
  
  //add a key event to move the player fish around
  // and update the location
  CartPt onKey(String key, int speed) {
    if (key.equals("up")) {
      int y = this.y - speed;
      if (y <= 0) {
        y = 800;
      }
      return new CartPt(x, y);
    }
    else if (key.equals("down")) {
      int y = this.y + speed;
      if (y >= 800) {
        y = 0;
      }
      return new CartPt(x, y);
    }
    else if (key.equals("left")) {
      int x = this.x - speed;
      if (x <= 0) {
        x = 400;
      }
      return new CartPt(x, y);
    }
    else if (key.equals("right")) {
      int x = this.x + speed;
      if (x >= 400) {
        x = 0;
      }
      return new CartPt(x, y);
    }
    
    return this;
  }
  
  //Returns whether the cartpts are the same
  public boolean isSameCartPt(CartPt that) {
    return this.x == that.x 
        && this.y == that.y;
  }
}

//Represents the area that a fish takes up
class Boundary {
  double minX;
  double maxX;
  double minY;
  double maxY;
  
  //constructs a Boundary given a specific x and y of the center,
  //image width, and image height
  Boundary(double x, double y, double width, double height) {
    this.minX = x - width / 2;
    this.maxX = x + width / 2;
    this.minY = y - height / 2;
    this.maxY = y + height / 2;
  }

  //constructs a Boundary given a specific CartPt,
  //image width, and image height
  Boundary(CartPt loc, double width, double height) {
    this.minX = loc.getX() - width / 2;
    this.maxX = loc.getX() + width / 2;
    this.minY = loc.getY() - height / 2;
    this.maxY = loc.getY() + height / 2;
  }
  
  /*
   * fields
   * this.minX ... double
   * this.maxX ... double
   * this.minY ... double
   * this.maxY ... double
   * 
   * method
   * this.overlappingBoundary(Boundary) ... boolean
   * this.isSameBoundary(Boundary) ... boolean
   */
  
  boolean overlappingBoundary(Boundary b) {
    Utils u = new Utils();
    //the xs have to overlap and the ys have to overlap
    return u.checkTwoRanges(this.minX, this.maxX, b.minX, b.maxX)
        && u.checkTwoRanges(this.minY, this.maxY, b.minY, b.maxY);
  }
  
  //Returns whether this is the same boundary as that
  public boolean isSameBoundary(Boundary that) {
    return this.minX == that.minX
        && this.minY == that.minY
        && this.maxX == that.maxX
        && this.maxY == that.maxY;
  }
  
}



//Examples for the fish game
class ExamplesFish {
  Random r = new Random(20);
  Random r2 = new Random(21);
  
  CartPt cp1 = new CartPt();
  CartPt cp2 = new CartPt();
  
  AFish player = new PlayerFish(1, new CartPt(300, 300));
  AFish player2 = new PlayerFish(1, new CartPt(350, 600));
  
  AFish bg1 = new BGFish(r);
  AFish bg2 = new BGFish(r);
  AFish bg3 = new BGFish(r);
  
  
  ILoFish mt = new MtLoFish();
  ILoFish bgFish = new ConsLoFish(this.bg1, 
      new ConsLoFish(this.bg2, new ConsLoFish(this.bg3, this.mt)));
  ILoFish bg2Fish = new ConsLoFish(this.bg2, 
      new ConsLoFish(this.bg3, this.mt));
  
  FishWorld fishWorld = new FishWorld(this.player, this.bgFish);
  FishWorld fishWorld2 = new FishWorld(this.player, this.mt);
  FishWorld fishWorld3 = new FishWorld(this.player2, this.bgFish);
  
  WorldScene ws = new WorldScene(400, 800);
  
  Boundary b1 = new Boundary(10, 20, 10, 20);
  Boundary b2 = new Boundary(90, 150, 30, 20);
  Boundary b1Over = new Boundary(15, 20, 10, 10);  //This one overlaps b1
  Boundary b3 = new Boundary(40, 40, 10, 10);
  Boundary b3Over = new Boundary(40, 40, 6, 6);
  
  //these three fish are used in overlapFish and all overlap each other
  AFish player3 = new PlayerFish(1, new CartPt(300,300));
  AFish player3Over = new PlayerFish(1, new CartPt(325, 300));
  AFish bg3Over = new BGFish(0.5, new CartPt(300, 300), new Random(20), 5);
  AFish bg4 = new BGFish(2, new CartPt(900, 900), new Random(20), 6);
  AFish bg5 = new BGFish(1.5, new CartPt(500, 500), new Random(20), 4);
  
  //for testing overlapFishInList
  ILoFish overlapBoth = new ConsLoFish(this.player3Over, new ConsLoFish(this.bg3Over, this.mt));
  ILoFish overlap1 = new ConsLoFish(this.player3Over, new ConsLoFish(this.bg4, this.mt));
  ILoFish overlapNone = new ConsLoFish(this.bg4, new ConsLoFish(this.bg5, this.mt));
  
  //for testing biggerthan
  AFish player4 = new PlayerFish(1, new CartPt(40,40));
  AFish bg6 = new BGFish(2, new CartPt(50, 40), new Random(20), 4);
  AFish bg7 = new BGFish(1.25, new CartPt(90,100), new Random(20), 3);
  ILoFish bgs = new ConsLoFish(this.bg6, this.mt);
  
  //for testing gameOver
  FishWorld gameOverWorld = new FishWorld(this.player4, this.bgs);
  
  //for testing isSameBoundary
  Boundary b4 = new Boundary(40, 50, 86, 98);
  Boundary b5 = new Boundary(40, 50, 86, 98);
  Boundary b6 = new Boundary(41, 50, 86, 98);
  
  //for testing isSameCartPt
  CartPt cp3 = new CartPt(90,100);
  CartPt cp4 = new CartPt(90,100);
  CartPt cp5 = new CartPt(80,100);
  CartPt cp6 = new CartPt(90,140);
  
  //for testing isSameFish
  AFish bg8 = new BGFish(1.4, new CartPt(30,40), new Random(20), 4);
  AFish bg9 = new BGFish(1.4, new CartPt(30,40), new Random(20), 4);
  AFish bg10 = new BGFish(1.4, new CartPt(30,40), new Random(20), -4);
  
  
  //for testing onTickForTesting
  AFish player5 = new PlayerFish(1, new CartPt(40,40));
  AFish bg11 = new BGFish(1.6, new CartPt(48, 48), new Random(20), -10);
  AFish bg12 = new BGFish(1.3, new CartPt(390, 28), new Random(20), -8);
  ILoFish bgFish2 = new ConsLoFish(this.bg11, new ConsLoFish(this.bg12, this.mt));
  FishWorld fishWorld4 = new FishWorld(this.player5, this.bgFish2);
  AFish player6 = new PlayerFish(2.5, new CartPt(40,40));
  
  //for testing moveCartPt
  CartPt cp_3 = new CartPt(300, 300);
  CartPt cp_4 = new CartPt(405, 300);
  CartPt cp_5 = new CartPt(-100, 200);
  
  // for testing move in ILoFish
  ILoFish unmoved = new ConsLoFish(this.bg1,
      new ConsLoFish(this.bg2, new ConsLoFish(this.bg3, this.mt)));
  ILoFish moved = new ConsLoFish(this.bg1.move(),
      new ConsLoFish(this.bg2.move(), new ConsLoFish(this.bg3.move(), this.mt)));

  // for testing atePlayer
  ILoFish hasBiggerFishes = new ConsLoFish(this.bg4, new ConsLoFish(this.bg5, this.mt));
  ILoFish noBiggerFish = new ConsLoFish(this.bg3Over, this.mt);
  ILoFish hasABiggerFish = new ConsLoFish(this.bg7, new ConsLoFish(this.bg3Over, this.mt));

  // for testing lastScene
  FishWorld winWorld = new FishWorld(new PlayerFish(2.6, new CartPt(100, 100)), new MtLoFish());
  FishWorld loseWorld = new FishWorld(new PlayerFish(1.0, new CartPt(100, 100)),
      new ConsLoFish(new BGFish(1.0, new CartPt(120, 120), new Random(), 1), new MtLoFish()));

  PlayerFish player7 = new PlayerFish(new Random(20));
   
   
  // tests fishOverlapping in World
  boolean testFishOverlapping(Tester t) {
    return t.checkExpect(this.overlapBoth.fishOverlapping(this.player3Over), this.overlapBoth)
        && t.checkExpect(this.overlapBoth.fishOverlapping(this.bg3Over), this.overlapBoth)
        && t.checkExpect(this.overlap1.fishOverlapping(this.bg4), new ConsLoFish(this.bg4, this.mt))
        && t.checkExpect(this.overlapNone.fishOverlapping(this.bg4),
            new ConsLoFish(this.bg4, this.mt))
        && t.checkExpect(this.overlapNone.fishOverlapping(this.bg5),
            new ConsLoFish(this.bg5, this.mt));
  }

  // tests removeFish
  boolean testRemoveFish(Tester t) {
    return t.checkExpect(this.mt.removeFish(this.mt), this.mt)
        && t.checkExpect(this.bgFish.removeFish(bgFish), this.mt)
        && t.checkExpect(this.bgFish.removeFish(bg2Fish), new ConsLoFish(this.bg1, this.mt));
  }

  // test checkEaten
  boolean testCheckEaten(Tester t) {
    return t.checkExpect(this.fishWorld.checkEaten(), new FishWorld(this.player, this.bgFish))
        && t.checkExpect(this.fishWorld2.checkEaten(), new FishWorld(this.player, this.mt))
        && t.checkExpect(this.fishWorld3.checkEaten(), new FishWorld(this.player2, this.bgFish));
  }
   
  // test move in CartPt
  boolean testMove(Tester t) {
    return t.checkExpect(this.cp_3.move(1), new CartPt(301, 300))
        && t.checkExpect(this.cp_4.move(1), new CartPt(0, 300))
        && t.checkExpect(this.cp_5.move(-1), new CartPt(400, 200));
  }

  // tests move in ILoFish
  boolean testMoveInILo(Tester t) {
    return t.checkExpect(this.mt.move(), this.mt) && t.checkExpect(this.unmoved.move(), this.moved);

  }

  //testing onkey
  // Test onKey method for PlayerFish
  boolean testOnKey(Tester t) {
    return t.checkExpect(this.player7.onKey("down"),
        new PlayerFish(this.player7.size, this.player7.location.onKey("down", this.player7.speed)))
        && t.checkExpect(this.player7.onKey("left"),
            new PlayerFish(this.player7.size,
                this.player7.location.onKey("left", this.player7.speed)))
        && t.checkExpect(this.player7.onKey("right"), new PlayerFish(this.player7.size,
            this.player7.location.onKey("right", this.player7.speed)));
  }
  
  // tests move in AFish
  boolean testMoveInAFish(Tester t) {
    return t.checkExpect(this.player3.move(),
        new PlayerFish(1, new CartPt((300 + this.player3.speed), 300)))
        && t.checkExpect(this.bg6.move(),
            new BGFish(2.0, new CartPt((50 + this.bg6.speed), 40), new Random(), 4))
        && t.checkExpect(this.bg7.move(),
            new BGFish(1.25, new CartPt((90 + this.bg7.speed), 100), new Random(), 3));
  }

  // for testing atePlayer
  boolean testAtePlayer(Tester t) {
    return t.checkExpect(this.hasBiggerFishes.atePlayer(this.player3), true)
        && t.checkExpect(this.noBiggerFish.atePlayer(this.player4), false)
        && t.checkExpect(this.mt.atePlayer(this.player3), false);
  }

  // tests contains method in ConsLoFish
  boolean testContains(Tester t) {
    return t.checkExpect(this.bgFish.contains(this.bg1), true)
        && t.checkExpect(this.bg2Fish.contains(this.bg3), true)
        && t.checkExpect(this.bg2Fish.contains(this.bg1), false)
        && t.checkExpect(this.mt.contains(this.bg1), false);
  }

  // testing ontick
  boolean testOnTickForTesting(Tester t) {
    return t.checkExpect(fishWorld4.onTickForTesting(), fishWorld4.endOfWorld("You lose!!"))
        && t.checkExpect(
            new FishWorld(this.player5, new ConsLoFish(this.bg12, this.mt)).onTickForTesting(),
            new FishWorld(this.player5,
                new ConsLoFish(this.bg12, this.mt).move().addLoFishForTesting(1)).checkEaten())
        && t.checkExpect(
            new FishWorld(this.player6, new ConsLoFish(this.bg12, this.mt)).onTickForTesting(),
            new FishWorld(this.player6,
                new ConsLoFish(this.bg12, this.mt).move().addLoFishForTesting(1)).checkEaten()
                .endOfWorld("You win!!"));
  }

  // testing isSameBoundary
  boolean testSameBoundary(Tester t) {
    return t.checkExpect(b4.isSameBoundary(this.b5), true)
        && t.checkExpect(b5.isSameBoundary(this.b4), true)
        && t.checkExpect(b6.isSameBoundary(this.b4), false)
        && t.checkExpect(b5.isSameBoundary(this.b6), false);
  }

  // testing isSameCartPt
  boolean testIsSameCartPt(Tester t) {
    return t.checkExpect(this.cp3.isSameCartPt(this.cp4), true)
        && t.checkExpect(this.cp4.isSameCartPt(this.cp3), true)
        && t.checkExpect(this.cp4.isSameCartPt(this.cp5), false)
        && t.checkExpect(this.cp6.isSameCartPt(this.cp5), false)
        && t.checkExpect(this.cp3.isSameCartPt(this.cp5), false);
  }

  // tests biggerThan
  boolean testBiggerThan(Tester t) {
    return t.checkExpect(this.bg6.biggerThan(this.player4), true)
        && t.checkExpect(this.bg7.biggerThan(this.player4), true)
        && t.checkExpect(this.bg7.biggerThan(this.bg6), false)
        && t.checkExpect(this.bg3Over.biggerThan(this.player4), false);
  }

  // tests addSize
  boolean testAddSize(Tester t) {
    return t.checkExpect(this.player.addSize(this.bg3Over),
        new PlayerFish(1 + 0.5 / 5, this.player.location))
        && t.checkExpect(this.player4.addSize(this.bg6),
            new PlayerFish(1 + 2.0 / 5, this.player4.location))
        && t.checkExpect(this.player3Over.addSize(this.bg7),
            new PlayerFish(1 + 1.25 / 5, this.player3Over.location))
        && t.checkExpect(this.player4.addSize(this.bg5),
            new PlayerFish(1 + 1.5 / 5, this.player4.location));
  }

  // Testing isSameFish
  boolean testIsSameFish(Tester t) {
    return t.checkExpect(this.player.isSameFish(this.player2), false)
        && t.checkExpect(this.bg8.isSameFish(this.bg9), true)
        && t.checkExpect(this.bg9.isSameFish(this.bg8), true)
        && t.checkExpect(this.bg8.isSameFish(this.player2), false);
  }

  // tests draw of background fish and player fish
  boolean testDrawBG(Tester t) {
    // We are accessing fields of fields here due to the fish being randomly
    // generated.
    return t.checkExpect(this.bg1.draw(this.ws),
        this.ws.placeImageXY(this.bg1.drawShape(), this.bg1.location.x, this.bg1.location.y))
        && t.checkExpect(this.bg2.draw(this.ws),
            this.ws.placeImageXY(this.bg2.drawShape(), this.bg2.location.x, this.bg2.location.y))
        && t.checkExpect(this.bg3.draw(this.ws),
            this.ws.placeImageXY(this.bg3.drawShape(), this.bg3.location.x, this.bg3.location.y))
        && t.checkExpect(this.player.draw(this.ws),
            this.ws.placeImageXY(this.player.drawShape(), 300, 300));
  }

  // draws the shape of all the fish
  boolean testDrawShape(Tester t) {
    return t
        .checkExpect(this.bg1.drawShape(),
            new ScaleImage(
                new OverlayOffsetImage(new EllipseImage(60, 30, OutlineMode.SOLID, Color.BLUE), -20,
                    0,
                    new RotateImage(new EquilateralTriangleImage(40, OutlineMode.SOLID, Color.BLUE),
                        90)),
                this.bg1.size))
        && t.checkExpect(this.player.drawShape(),
            new ScaleImage(new OverlayOffsetImage(
                new EllipseImage(60, 30, OutlineMode.SOLID, Color.ORANGE), -20, 0,
                new RotateImage(new EquilateralTriangleImage(40, OutlineMode.SOLID, Color.ORANGE),
                    90)),
                1))
        && t.checkExpect(this.bg10.drawShape(), new RotateImage(
            new ScaleImage(
                new OverlayOffsetImage(new EllipseImage(60, 30, OutlineMode.SOLID, Color.BLUE), -20,
                    0, new RotateImage(
                        new EquilateralTriangleImage(40, OutlineMode.SOLID, Color.BLUE), 90)),
                1.4),
            180));
  }

  // tests draw for ILoFish
  boolean testDrawILoFish(Tester t) {
    return t.checkExpect(this.mt.draw(this.ws), this.ws)
        && t.checkExpect(this.bgFish.draw(ws), this.bg3.draw(this.bg2.draw(this.bg1.draw(this.ws))))
        && t.checkExpect(this.bg2Fish.draw(ws), this.bg3.draw(this.bg2.draw(this.ws)));
  }

  // tests makeScene in FishWorld
  boolean testMakeScene(Tester t) {
    return t.checkExpect(this.fishWorld.makeScene(),
        this.player.draw(this.bgFish.draw(new WorldScene(400, 800))))
        && t.checkExpect(this.fishWorld2.makeScene(), this.player.draw(new WorldScene(400, 800)))
        && t.checkExpect(this.fishWorld3.makeScene(),
            this.player2.draw(this.bgFish.draw(new WorldScene(400, 800))));
  }
  
  // tests drawShapeOnScene in CartPt
  boolean testDrawShapeOnScene(Tester t) {
    return t.checkExpect(new CartPt(250, 350).drawShapeOnScene(this.ws, this.bg3.drawShape()),
        this.ws.placeImageXY(this.bg3.drawShape(), 250, 350))
        && t.checkExpect(new CartPt(300, 400).drawShapeOnScene(this.ws, this.bg2.drawShape()),
            this.ws.placeImageXY(this.bg2.drawShape(), 300, 400))
        && t.checkExpect(
            new CartPt(500, 600).drawShapeOnScene(
                new CartPt(300, 400).drawShapeOnScene(this.ws, this.bg2.drawShape()),
                this.bg3.drawShape()),
            this.ws.placeImageXY(this.bg2.drawShape(), 300, 400).placeImageXY(this.bg3.drawShape(),
                500, 600));
  }

  // tests generateLoFish
  boolean testAddLoFish(Tester t) {
    return t.checkExpect(this.mt.addLoFishForTesting(3),
        new ConsLoFish(new BGFish(new Random(20)),
            new ConsLoFish(new BGFish(new Random(20)),
                new ConsLoFish(new BGFish(new Random(20)), new MtLoFish()))))
        && t.checkExpect(this.mt.addLoFishForTesting(0), this.mt)
        && t.checkExpect(this.bgFish.addLoFishForTesting(2),
            new ConsLoFish(this.bg1,
                new ConsLoFish(this.bg2,
                    new ConsLoFish(this.bg3, new ConsLoFish(new BGFish(new Random(20)),
                        new ConsLoFish(new BGFish(new Random(20)), this.mt))))));
  }

  // tests getX and getY
  boolean testGetXY(Tester t) {
    return t.checkExpect(this.cp1.getX(), this.cp1.x) && t.checkExpect(this.cp1.getY(), this.cp1.y)
        && t.checkExpect(this.cp2.getX(), this.cp2.x) && t.checkExpect(this.cp2.getY(), this.cp2.y);
  }

  // tests overlappingBoundary
  boolean testOverlappingBoundary(Tester t) {
    return t.checkExpect(this.b1.overlappingBoundary(this.b2), false)
        && t.checkExpect(this.b2.overlappingBoundary(this.b1), false)
        && t.checkExpect(this.b1.overlappingBoundary(this.b1Over), true)
        && t.checkExpect(this.b1Over.overlappingBoundary(this.b1), true)
        && t.checkExpect(this.b3.overlappingBoundary(this.b3Over), true)
        && t.checkExpect(this.b3Over.overlappingBoundary(this.b3), true);
  }

  // tests overlapFish
  boolean testOverlapFish(Tester t) {
    return t.checkExpect(this.player3.overlapFish(this.player3Over), true)
        && t.checkExpect(this.player3Over.overlapFish(this.player3), true)
        && t.checkExpect(this.player3.overlapFish(this.bg3Over), true)
        && t.checkExpect(this.bg3Over.overlapFish(this.player3), true)
        && t.checkExpect(this.player3Over.overlapFish(this.bg3Over), true)
        && t.checkExpect(this.bg3Over.overlapFish(this.player3Over), true)

        && t.checkExpect(this.bg4.overlapFish(this.player3Over), false)
        && t.checkExpect(this.player3Over.overlapFish(this.bg4), false)
        && t.checkExpect(this.bg4.overlapFish(this.bg3Over), false)
        && t.checkExpect(this.bg3Over.overlapFish(this.bg4), false);

  }

  // tests overlapFishInList
  boolean testOverlapFishInList(Tester t) {
    return t.checkExpect(this.overlapBoth.overlapFishInList(this.player3), true)
        && t.checkExpect(this.overlap1.overlapFishInList(this.player3), true)
        && t.checkExpect(this.overlapNone.overlapFishInList(this.player3), false)
        && t.checkExpect(this.mt.overlapFishInList(this.player3), false);
  }

  // tests checkTwoRanges
  boolean testCheckTwoRanges(Tester t) {
    Utils u = new Utils();
    return t.checkExpect(u.checkTwoRanges(0, 5, 4, 10), true)
        && t.checkExpect(u.checkTwoRanges(0, 10, 5, 6), true)
        && t.checkExpect(u.checkTwoRanges(0, 10, 15, 16), false);
  }

  boolean testBigBang(Tester t) {
    FishWorld world = new FishWorld();
    int worldWidth = 400;
    int worldHeight = 800;
    double tickRate = .1;
    return world.bigBang(worldWidth, worldHeight, tickRate);
  }
}