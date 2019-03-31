include <../../openscad-parts/tools.scad>
/* doing */
  $fn=40;

// raisers();
motor_block();

module raisers(height=4,num=8) {
  walls = 0.48;
  permimeters = 3;
  innerD = 3.3;
  outderD = innerD + 2 * permimeters * walls;
  gap = outderD + 1;

  for ( itemX = [0:num -1]) {
    for ( itemY = [0:num -1]) {
      translate([(itemX + 0.5 - num/2) * gap, (itemY + 0.5 - num/2) * gap, height/2])
        make_bearing(d1 = innerD, d2 = outderD, height = 4);
    }
  }
}
  mb_screw_offset = 17.2;
  mb_screw_D = 3.2;
  mb_motorH = 22.4;

  mb_box_X = 8;
  mb_box_Y = 10;
  mb_box_Z = mb_motorH;

  mb_mount_box_X = mb_box_X;
  mb_mount_box_Y = mb_box_Y + 15;
  mb_mount_box_Z = 5;

  mb_mount_box_offset_Z = mb_box_Z - mb_mount_box_Z/2;

  mb_mount_box_screw_offset = mb_mount_box_Y - 7;


module motor_block() {
  difference() {
    union() {

      // main box
      translateZ(mb_box_Z/2)
      ccube([mb_box_X, mb_box_Y, mb_box_Z]);


      // chassis screw block 
      translateZ(mb_mount_box_offset_Z)
      ccube([mb_mount_box_X, mb_mount_box_Y, mb_mount_box_Z]);

    }
    #motor_block_cutouts();
  }
}


module motor_block_cutouts() {
  // motor screws
  translateZ(mb_box_Z/2 + mb_screw_offset/2) {
    rotateY(90) {
      ccylinder(h = 20, d =  mb_screw_D);
    }
  }
  translateZ(mb_box_Z/2 - mb_screw_offset/2) {
    rotateY(90) {
      ccylinder(h = 20, d =  mb_screw_D);
    }
  }

  // mount screws
  translate([0, mb_mount_box_screw_offset/2, mb_mount_box_offset_Z])
  ccylinder(d = mb_screw_D, h = mb_mount_box_Z + 1);
  translate([0, -mb_mount_box_screw_offset/2, mb_mount_box_offset_Z])
  ccylinder(d = mb_screw_D, h = mb_mount_box_Z + 1);

}
