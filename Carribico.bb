AppTitle "Carribico"

Graphics3D 1024,768,32,2
SetBuffer BackBuffer()
Global frameTimer = CreateTimer(60)


Global cam = CreateCamera()
PositionEntity cam,64,25,-5
CameraClsColor cam,0,80,255
Global light = CreateLight()
RotateEntity light,0,0,45

;water
Global water = CreatePlane()
Global waterTexture = LoadTexture("gfx/water.png")
EntityTexture water, waterTexture

;very basic island
Global island = LoadTerrain("gfx/heightmap.png")
Global grassTexture = LoadTexture("gfx/grass.png")
ScaleEntity island,1,10,1
EntityTexture island, grassTexture
TerrainShading island,True



;MAIN LOOP
While(Not(KeyHit(1)))
  Cls()
  
  UpdateWorld()
  RenderWorld()
  
  Flip 0
Wend
;~IDEal Editor Parameters:
;~C#Blitz3D