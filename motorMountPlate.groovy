double screwHeightOfset = 1.0/8.0*25.4
CSGDatabase.clear()
LengthParameter printerOffset = new LengthParameter("printerOffset",0.25,[2,0.001])
printerOffset.setMM(0.25);

motor=(CSG)ScriptingEngine
	                    .gitScriptRun(
                                "https://github.com/WPIRoboticsEngineering/RBELabCustomParts.git", // git location of the library
	                              "gb37y3530-50en.groovy" , // file to load
	                              null
                        )
					.movez(-5.8)

CSG vshaft =  (CSG)ScriptingEngine
	                    .gitScriptRun(
                                "https://github.com/WPIRoboticsEngineering/RBELabCustomParts.git", // git location of the library
	                              "vexShaft.groovy" , // file to load
	                              [60]
                        )				
double vexHoleSpacing = 0.5*25.4
double vexSquare = 0.182
CSG screwKeepaway =new Cylinder(5,5,screwHeightOfset,(int)30).toCSG() // a one line Cylinder
CSG gear =Vitamins.get("vexGear","36T")
		//.roty(180)
		.toZMin()
		.movez(screwHeightOfset)
		.union(screwKeepaway)
		.difference(motor)
		
CSG mesh = Vitamins.get("vexGear","HS12T")
			.difference(vshaft)
			
int gearRadiusIndex = (int)((gear.getMaxX()+mesh.getMaxX())/vexHoleSpacing)
println gearRadiusIndex
mesh=mesh.movex(vexHoleSpacing*gearRadiusIndex)
CSG spacer = Vitamins.get("vexSpacer","oneEighth")
			.toZMax()
			.movex(vexHoleSpacing*gearRadiusIndex)
HashMap<String,Object> vexSpacerConfig = Vitamins.getConfiguration( "vexSpacer","oneEighth")
double innerRadius = vexSpacerConfig.innerDiameter/2
CSG vexHole = new Cylinder(innerRadius, innerRadius, vexHoleSpacing*2, 10).toCSG()
				.toZMax()

CSG vexHoleSet = vexHole

int width = 4
int length = 2

for(int i=0;i<length;i++){
	for(int j=-(width/2);j<(width/2+1);j++){
		vexHoleSet=vexHoleSet.union(vexHole
					.movex((i*vexHoleSpacing)+vexHoleSpacing)
					.movey(j*vexHoleSpacing)
					)
	}
}

CSG mountPlate = new Cube(vexHoleSpacing*(length+2.5),vexHoleSpacing*(width+1),vexHoleSpacing/2).toCSG()
				.toZMax()
				.toXMin()
				.movex(-vexHoleSpacing*2)
				.difference([motor,vexHoleSet])
mountPlate.setMfg( {toMfg ->
		toMfg.rotx(180)
			.toZMin()
})
gear.setMfg( {toMfg ->
		toMfg.rotx(180)
			.toZMin()
})				
return [gear,motor,new Cylinder(3,3,10,(int)30).toCSG()]
