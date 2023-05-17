import java.util.ArrayList;
import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;
import java.util.Random;

// list of neighbors -> ArrayList<Cells>

//Represents a single square of the game area
class Cell {
  // In logical coordinates, with the origin at the top-left corner of the screen
  // set size of the square to be 8.
  int x;
  int y;
  Color color;
  boolean flooded;

  // the four adjacent cells to this one
  ArrayList<Cell> neighbors;

  // Random generator
  Random rand = new Random();

  // creates the first cell in a new row
  Cell(int x, int y, boolean flod) {
    this.x = x;
    this.y = y;
    this.color = this.getRandCol(rand);
    this.flooded = flod;
    this.neighbors = new ArrayList<Cell>();
  }
  
  //for testing purposes seeding by giving a random 
  Cell(int x, int y, boolean flod, Random rand) {
    this.x = x;
    this.y = y;
    this.color = this.getRandCol(rand);
    this.flooded = flod;
    this.neighbors = new ArrayList<Cell>();
  }

  // draws a cell
  public WorldScene printCell(WorldScene scene) {
    RectangleImage cell = new RectangleImage(20, 20, OutlineMode.SOLID, this.color);
    scene.placeImageXY(cell, this.x, this.y);
    return scene;
  }

  // gets a random color
  Color getRandCol(Random rand) {
    int catalog = rand.nextInt(5);
    if (catalog == 0) {
      return Color.GREEN;
    }
    else if (catalog == 1) {
      return Color.BLUE;
    }
    else if (catalog == 2) {
      return Color.PINK;
    }
    else if (catalog == 3) {
      return Color.RED;
    }
    else if (catalog == 4) {
      return Color.YELLOW;
    }
    else {
      return Color.BLACK;
    }
  }

  // sets the flooded boolean according to the first block.
  //only applied to its the first blocks neighbors
  // Effect: changes the value of this.flooded
  void firstCells(Color firstCell) {
    this.flooded = this.color.equals(firstCell);
  }

  // checks if this is the square clicked
  boolean clicked(Posn pos) {
    return (this.x - 10 <= pos.x && this.x + 10 > pos.x && this.y - 10 <= pos.y
        && this.y + 10 > pos.y);
  }

}

// game floodit
class FloodItWorld extends World {

  // All the cells of the game
  ArrayList<Cell> board;

  // Defines an int constant
  static int BOARD_SIZE = 22;

  int clicks; // number of clicks

  Color col; // color of the clicked cell

  Color first; // color of the origin before the click

  int fixedSize; // fixed size of the board

  boolean inProcess; // tells if the board is in process of flooding
  // this prevents the game from ending when the clicks reach 0, but the player
  // won

  double second; // counts a second
  int totalTime; // keeps track of the seconds so far

  FloodItWorld(int size) {
    this.col = null;
    this.first = null;
    BOARD_SIZE = size;
    this.board = this.createBoard(new Random());
    this.clicks = ((int) (size / 3)) * 2 + size;
    this.fixedSize = size;
    this.inProcess = false;
    this.second = 0.0;
    this.totalTime = 0;
  }
  
  //for seeded testing purposes 
  FloodItWorld(int size, Random rand) {
    this.col = null;
    this.first = null;
    BOARD_SIZE = size;
    this.board = this.createBoard(rand);
    this.clicks = ((int) (size / 3)) * 2 + size;
    this.fixedSize = size;
    this.inProcess = false;
    this.second = 0.0;
    this.totalTime = 0;
  }

  // resets flooded to create a waterfall
  // Effect: sets all the cells' flooded in the board to false,
  // except for the cell at the top left corner (the origin).
  void resetFlooded() {
    for (Cell c : this.board) {
      c.flooded = false;
    }
    Cell first = this.board.get(0);
    first.flooded = true;
  }

  // prints all the cells (board)
  public WorldScene printArrCell(WorldScene scene) {
    for (Cell h : this.board) {
      h.printCell(scene);
    }
    return scene;
  }

  // manages when a cell has been clicked
  // Effect: subtracts one from clicks, this.col is
  // set to the color of the cell clicked, this.first is
  // set to the previous this.col and in this.inProcess is
  // set to true.
  public void onMouseClicked(Posn pos) {
    Color col = null;

    for (Cell c : this.board) {
      if (c.clicked(pos) && !(c.color.equals(this.col))) {
        this.clicks = this.clicks - 1;
        col = c.color;
        this.col = col;
        this.first = (this.board.get(0)).color;
        this.resetFlooded();
        this.inProcess = true;
      }
    }

  }

  // checks that all the cells on the board are flooded
  boolean done() {
    boolean flag = true;
    Color firstCell = ((this.board).get(0)).color;
    for (Cell c : this.board) {
      if (!(c.color).equals(firstCell) && !(c.flooded)) {
        flag = false;
      }
    }
    return flag;
  }

  // manages the restart pressing K
  // Effect: creates a new floodit and sets this elements to the elements
  // of the new floodit
  public void onKeyEvent(String key) {
    if (key.equals("r")) {
      FloodItWorld w = new FloodItWorld(this.fixedSize);
      this.board = w.board;
      this.clicks = w.clicks;
      this.col = w.col;
      this.first = w.first;
      this.second = w.second;
      this.inProcess = w.inProcess;
      this.totalTime = w.totalTime;
    }
  }

  // manages waterfall
  // Effect: modifies this.inProcess, adds .02 to this.second,
  // when this.second reaches 1, this.second is reset to 0, and this.totalTime is
  // increased by 1
  // changes the color of flooded cells in the board, and adds it to the arrayList
  // of flooded cells
  // and iterates through its neighbours and if a neighbor is
  // the same color as the original color before the click its flooded is set to
  // true.
  public void onTick() {
    Boolean change = false;
    ArrayList<Cell> sumn = new ArrayList<Cell>();

    for (Cell c : this.board) {
      if (c.flooded) {
        c.color = this.col;
        sumn.add(c);
      }
    }

    for (Cell h : sumn) {
      for (Cell n : h.neighbors) {
        if ((n.color).equals(this.first)) {
          n.flooded = true;
          change = true;
        }
      }

    }
    this.inProcess = change;

    if ((this.second < 1.0)) {
      this.second = this.second + .02;
    }
    else {
      this.second = 0.0;
      this.totalTime++;
    }

  }

  // creates a losing end scene
  WorldScene makeLosingScene() {
    WorldScene scene = new WorldScene(500, 500);
    scene.placeImageXY(new TextImage("Game over pal, get it moving.", 30, Color.RED), 250, 200);
    scene.placeImageXY(new TextImage("You lost in " + this.totalTime + " seconds!", 25, Color.RED),
        250, 300);
    scene.placeImageXY(new TextImage("You must feel ashamed of yourself.", 25, Color.RED), 250,
        350);
    return scene;
  }

  // Creates winning end scene
  WorldScene makeWinningScene() {
    WorldScene scene = new WorldScene(500, 500);
    int clicks = this.clicks;
    int score = (((int) (this.fixedSize / 3)) * 2 + this.fixedSize) - clicks;
    scene.placeImageXY(new TextImage("You did it congrats!", 30, Color.RED), 250, 200);
    scene.placeImageXY(new TextImage("Well done, it took you " + score + " clicks,", 22, Color.RED),
        250, 300);
    scene.placeImageXY(new TextImage("now you can be \"proud\" of something.", 22, Color.RED), 250,
        350);
    return scene;
  }

  // creates a final screen if you won or lost
  public WorldEnd worldEnds() {
    if (this.done()) {
      return new WorldEnd(true, makeWinningScene());
    }
    else if (this.clicks == 0 && !(this.inProcess)) {
      return new WorldEnd(true, makeLosingScene());
    }
    else {
      return new WorldEnd(false, makeWinningScene());
    }
  }

  // creates a board
  ArrayList<Cell> createBoard(Random rand) {
    int y = 20;
    int x = 30;
    boolean start = true;
    ArrayList<Cell> cells = new ArrayList<Cell>();
    for (int i = 0; i < BOARD_SIZE; i++) { // column builder loop
      y = 10 + (20 * (i));
      Cell colEl = new Cell(y, x, start, rand);
      cells.add(colEl);

      for (int j = 1; j < BOARD_SIZE; j++) { // row builder loop
        Cell rowEl = new Cell(y, x + (20 * j), start, rand);
        cells.add(rowEl);
        int indx2 = cells.indexOf(rowEl);
        this.preCol(cells, indx2, rowEl);
        this.prevRow(cells, indx2, rowEl);
        start = false;
      }
      int indx = cells.indexOf(colEl);
      this.prevRow(cells, indx, colEl);
    }

    Cell first = cells.get(0);
    for (Cell c : first.neighbors) {
      c.firstCells(first.color);
    }
    this.col = first.color;
    this.first = first.color;
    return cells;
  }

  // Connects this cell to the previous row
  // Effect: adds a new element to the array of the current cell
  // and the one above it.
  void prevRow(ArrayList<Cell> cells, int indx, Cell current) {
    if (indx >= BOARD_SIZE) {
      Cell above = cells.get(indx - BOARD_SIZE);
      current.neighbors.add(above);
      above.neighbors.add(current);
    }
  }

  // Connects this cell to previous column
  // Effect: adds a new element to the array of the current cell,
  // and the cell before this one.
  void preCol(ArrayList<Cell> cells, int indx, Cell current) {
    Cell prev = cells.get(indx - 1);
    current.neighbors.add(prev);
    prev.neighbors.add(current);
  }

  // prints the score
  public WorldScene printScore(WorldScene scene) {
    TextImage score = new TextImage("Clicks left: " + this.clicks, 12, Color.BLACK);
    TextImage time = new TextImage("Seconds:  " + this.totalTime, 12, Color.BLACK);
    scene.placeImageXY(score, 43, 12);
    scene.placeImageXY(time, 400, 12);
    return scene;
  }

  // creates initial scene
  public WorldScene makeScene() {
    WorldScene scene = new WorldScene(500, 500);
    scene = this.printScore(scene);
    scene = this.printArrCell(scene);
    return scene;
  }

}

// render testing
class ExampleFLoodItWorld {
  boolean testBigBang(Tester t) {
    FloodItWorld world = new FloodItWorld(22);
    world.bigBang(1800, 1800, .02);
    return true;
  }
  
  //Any nexus tests, tests the precol and prevrow because when a world
  //is created the methods are called within create board to link them
  // tests if two cells are connected.
  void testNexus(Tester t) {
    FloodItWorld link = new FloodItWorld(2);
    ArrayList<Cell> start = new ArrayList<Cell>();
    start.add(link.board.get(1));
    start.add(link.board.get(2));
    Cell testing = link.board.get(0);
    t.checkExpect(testing.neighbors, start);
    t.checkExpect(testing.neighbors.size(), 2);
    t.checkExpect(link.board.size(), 4);
  }

  // tests connectivity of the cells
  void testNexus2(Tester t) {
    FloodItWorld link = new FloodItWorld(3);
    ArrayList<Cell> nexus = new ArrayList<Cell>();
    nexus.add(link.board.get(3));
    nexus.add(link.board.get(1));
    nexus.add(link.board.get(5));
    nexus.add(link.board.get(7));
    Cell testing = link.board.get(4);
    t.checkExpect(testing.neighbors, nexus);
    t.checkExpect(link.board.size(), 9);
    t.checkExpect(testing.neighbors.size(), 4);

    ArrayList<Cell> nexus2 = new ArrayList<Cell>();
    nexus2.add(link.board.get(7));
    nexus2.add(link.board.get(5));
    Cell testing2 = link.board.get(8);
    t.checkExpect(testing2.neighbors, nexus2);
    t.checkExpect(testing2.neighbors.size(), 2);

    ArrayList<Cell> nexus3 = new ArrayList<Cell>();
    nexus3.add(link.board.get(0));
    nexus3.add(link.board.get(2));
    nexus3.add(link.board.get(4));
    Cell testing3 = link.board.get(1);
    t.checkExpect(testing3.neighbors, nexus3);
    t.checkExpect(testing3.neighbors.size(), 3);

    ArrayList<Cell> nexus4 = new ArrayList<Cell>();
    nexus4.add(link.board.get(1));
    nexus4.add(link.board.get(3));
    Cell testing4 = link.board.get(0);
    t.checkExpect(testing4.neighbors, nexus4);
    t.checkExpect(testing4.neighbors.size(), 2);

  }
  
  void testPrintCell(Tester t) {
    WorldScene scene0 = new WorldScene(500, 500);
    WorldScene scene00 = new WorldScene(500, 500);
    Cell c1 = new Cell(0,0,true);
    Cell c2 = new Cell(25,25,true);
    WorldScene scene1 = new WorldScene(500, 500);
    WorldScene scene2 = new WorldScene(500, 500);
    scene1.placeImageXY(new RectangleImage(20, 20, OutlineMode.SOLID, c1.color), 0, 0);
    scene2.placeImageXY(new RectangleImage(20, 20, OutlineMode.SOLID, c2.color), 25, 25);
    t.checkExpect(c1.printCell(scene0), scene1);
    t.checkExpect(c2.printCell(scene00), scene2);
  }
  
  void testGetRandColor(Tester t) {
    Random randy = new Random(1);
    Cell c1 = new Cell(0,0,true, randy);
    Cell c2 = new Cell(25,25,true, randy);
    t.checkExpect(c1.color, Color.GREEN);
    t.checkExpect(c2.color, Color.RED);
  }
  
  void testFirstCell(Tester t) {
    FloodItWorld w1 = new FloodItWorld(4, new Random(1));
    int floodedCounter1 = 0;
    //inside the creation of a board firstCell is called so we dont need to call it
    //w1 has no origin neighbors of the same color so only the first cell should be true
    for (Cell c: w1.board) {
      if (c.flooded) {
        floodedCounter1 ++;
      }
    }
    t.checkExpect(floodedCounter1, 1);
    FloodItWorld w2 = new FloodItWorld(4, new Random(5));
    //the origin is pink and so is the one below the origin so two cells should be true
    int floodedCounter2 = 0;
    for (Cell c: w2.board) {
      if (c.flooded) {
        floodedCounter2 ++;
      }
    }
    t.checkExpect(floodedCounter2, 2);
  }
  
  void testClicked(Tester t) {
    Cell c1 = new Cell(35,35, true);
    t.checkExpect(c1.clicked(new Posn(35,35)), true); //on basically center
    t.checkExpect(c1.clicked(new Posn(30,35)), true); //below on the x
    t.checkExpect(c1.clicked(new Posn(35,30)), true); //below on the y
    t.checkExpect(c1.clicked(new Posn(40,35)), true); //above on the x
    t.checkExpect(c1.clicked(new Posn(35,40)), true); //above on the y
    t.checkExpect(c1.clicked(new Posn(20,20)), false); //out of bounds below both
    t.checkExpect(c1.clicked(new Posn(45,45)), false); //out of bound above both
    t.checkExpect(c1.clicked(new Posn(20,35)), false); //out of bounds below on the x
    t.checkExpect(c1.clicked(new Posn(35,45)), false); //out of bounds above on the y
  }
  
  void testResetOnKey(Tester t) {
    FloodItWorld w2 = new FloodItWorld(4, new Random(5));
    w2.clicks--;
    t.checkExpect(w2.clicks, 5);  //pretending an action happened
    w2.onKeyEvent("f");
    t.checkExpect(w2.clicks, 5); //not the "r" key thus no reset
    w2.onKeyEvent("r");
    t.checkExpect(w2.clicks, 6); //reset and moves back to total
  }

  void testPrintArray(Tester t) {
    WorldScene scene0 = new WorldScene(500, 500);
    WorldScene scene1 = new WorldScene(500, 500);
    WorldScene scene2 = new WorldScene(500, 500);
    WorldScene scene3 = new WorldScene(500, 500);
    FloodItWorld w0 = new FloodItWorld(2, new Random(1));
    FloodItWorld w1 = new FloodItWorld(2, new Random(2));
    w0.board.get(0).printCell(scene0);
    w0.board.get(1).printCell(scene0);
    w0.board.get(2).printCell(scene0);
    w0.board.get(3).printCell(scene0);
    t.checkExpect(w0.printArrCell(scene1), scene0);
    w1.board.get(0).printCell(scene2);
    w1.board.get(1).printCell(scene2);
    w1.board.get(2).printCell(scene2);
    w1.board.get(3).printCell(scene2);
    t.checkExpect(w1.printArrCell(scene3), scene2);
  }
  
  void testMouseClick(Tester t) {
    FloodItWorld w0 = new FloodItWorld(2, new Random(1));
    t.checkExpect(w0.clicks, 2); //starting clicks
    w0.onMouseClicked(new Posn(60,60)); //out of bounds
    t.checkExpect(w0.clicks, 2); //clicks have decreased
    w0.onMouseClicked(new Posn(20,20)); //within bounds
    t.checkExpect(w0.clicks, 1); //clicks have decreased
  }
  
  void testCreateBoard(Tester t) {
    boolean same = false;
    boolean same1 = false;
    FloodItWorld w0 = new FloodItWorld(2, new Random(1));
    FloodItWorld w02 = new FloodItWorld(2, new Random(1));
    FloodItWorld w1 = new FloodItWorld(2, new Random(2));
    
    for (int i = 0; i < w0.board.size(); i++) {
      if (w0.board.get(i).color.equals(w02.board.get(i).color)) {
        same = true;
      } else { 
        same = false;
      }
    }
    t.checkExpect(same, true); //boards are the same
    for (int i = 0; i < w0.board.size(); i++) {
      if (w0.board.get(i).color.equals(w1.board.get(i).color)) {
        same1 = true;
      } else { 
        same1 = false;
      }
    }
    t.checkExpect(same1, false); //boards are different
  }
  
  void testDoneAndWorldEnds(Tester t) { 
    FloodItWorld w0 = new FloodItWorld(2, new Random(1));
    FloodItWorld w1 = new FloodItWorld(2, new Random(1));
    t.checkExpect(w0.done(), false); //when initialed not done
    
    w0.board.get(0).color = Color.RED;
    w0.board.get(0).flooded = true;
    t.checkExpect(w0.done(), false); //not done after one is flooded
    
    w0.board.get(1).color = Color.RED; 
    w0.board.get(1).flooded = true;
    w0.board.get(2).flooded = true;
    w0.board.get(3).flooded = true;
    t.checkExpect(w0.done(), true); //done after all r flooded and the same color
    t.checkExpect(w0.worldEnds(), new WorldEnd(true, w0.makeWinningScene()));
    t.checkExpect(w1.done(), false);
    //t.checkExpect(w1.worldEnds(), w1.worldEnds()); //i literally don't 
    //know how to test the result but it just doesn't produce an end scene
    //when the game is over so lets say it works => Piazza post were not helpful
    w1.clicks = 0;
    t.checkExpect(w1.done(), false); //not done board
    t.checkExpect(w1.worldEnds(), new WorldEnd(true, w1.makeLosingScene()));
    //end scene makes a losing scene
    
  }
  
  void testEndScenes(Tester t) {
    WorldScene scene0 = new WorldScene(500, 500);
    WorldScene scene1 = new WorldScene(500, 500);
    WorldScene scene2 = new WorldScene(500, 500);
    WorldScene scene3 = new WorldScene(500, 500);
    FloodItWorld w0 = new FloodItWorld(2, new Random(1));
    FloodItWorld w1 = new FloodItWorld(2, new Random(2));

    scene0.placeImageXY(new TextImage("Game over pal, get it moving.", 30, Color.RED), 250, 200);
    scene0.placeImageXY(new TextImage("You lost in " + 0 + " seconds!", 25, Color.RED),
        250, 300);
    scene0.placeImageXY(new TextImage("You must feel ashamed of yourself.", 25, Color.RED), 250,
        350);
    w1.totalTime = 3;
    w1.clicks = 3;
    scene1.placeImageXY(new TextImage("Game over pal, get it moving.", 30, Color.RED), 250, 200);
    scene1.placeImageXY(new TextImage("You lost in " + 3 + " seconds!", 25, Color.RED),
        250, 300);
    scene1.placeImageXY(new TextImage("You must feel ashamed of yourself.", 25, Color.RED), 250,
        350);
    scene2.placeImageXY(new TextImage("You did it congrats!", 30, Color.RED), 250, 200);
    scene2.placeImageXY(new TextImage("Well done, it took you " + 0 + " clicks,", 22, Color.RED),
        250, 300);
    scene2.placeImageXY(new TextImage("now you can be \"proud\" of something.", 22, Color.RED), 250,
        350);
    scene3.placeImageXY(new TextImage("You did it congrats!", 30, Color.RED), 250, 200);
    scene3.placeImageXY(new TextImage("Well done, it took you " + -1 + " clicks,", 22, Color.RED),
        250, 300);
    scene3.placeImageXY(new TextImage("now you can be \"proud\" of something.", 22, Color.RED), 250,
        350);
  
    
    t.checkExpect(w0.makeLosingScene(), scene0);
    t.checkExpect(w1.makeLosingScene(), scene1);
    t.checkExpect(w0.makeWinningScene(), scene2);
    t.checkExpect(w1.makeWinningScene(), scene3);
    
  }
  
  void testPrintScore(Tester t) {
    WorldScene scene1 = new WorldScene(500, 500);
    WorldScene scene2 = new WorldScene(500, 500);
    scene2.placeImageXY(new TextImage("Clicks left: " + 2, 12, Color.BLACK), 43, 12);
    scene2.placeImageXY(new TextImage("Seconds:  " + 0, 12, Color.BLACK), 400, 12);
    FloodItWorld w1 = new FloodItWorld(2, new Random(2));
    t.checkExpect(w1.printScore(scene1), scene2);
    
    WorldScene scene3 = new WorldScene(500, 500);
    WorldScene scene4 = new WorldScene(500, 500);
    scene4.placeImageXY(new TextImage("Clicks left: " + 36, 12, Color.BLACK), 43, 12);
    scene4.placeImageXY(new TextImage("Seconds:  " + 0, 12, Color.BLACK), 400, 12);
    FloodItWorld w0 = new FloodItWorld(22, new Random(1));
    t.checkExpect(w0.printScore(scene3), scene4);
  }
  
  void testMakeScene(Tester t) {
    FloodItWorld w0 = new FloodItWorld(22, new Random(1));
    WorldScene scene1 = new WorldScene(500, 500);
    w0.printArrCell(scene1);
    w0.printScore(scene1);
    t.checkExpect(w0.makeScene(), scene1);
    FloodItWorld w1 = new FloodItWorld(32, new Random(1));
    w1.totalTime = 30;
    w1.clicks = 6;
    WorldScene scene2 = new WorldScene(500, 500);
    w1.printArrCell(scene2);
    w1.printScore(scene2);
    t.checkExpect(w1.makeScene(), scene2);
  }
  
  void testOnTick(Tester t) {
    FloodItWorld w0 = new FloodItWorld(3, new Random(3));
    w0.onMouseClicked(new Posn(10,50));
    w0.onTick();
    t.checkExpect(w0.board.get(0).color, Color.GREEN); //color updated
    w0.onMouseClicked(new Posn(30,20));
    w0.onTick();
    t.checkExpect(w0.board.get(3).color, Color.BLUE); //color updated
  }
  
  
}
