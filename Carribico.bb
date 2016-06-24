AppTitle "Carribico"
SeedRnd MilliSecs()

Global screenWidth = 1024
Global screenHeight = 768
Global colorDepth = 32
Global screenMode = 2

;Constants
Const KEY_LEFT = 203
Const KEY_RIGHT = 205
Const KEY_UP = 200
Const KEY_DOWN = 208
Const KEY_W = 17
Const KEY_A = 30
Const KEY_S = 31
Const KEY_D = 32

Const MOUSE_WHEEL = 3


Graphics3D screenWidth,screenHeight,colorDepth,screenMode
SetBuffer BackBuffer()
Global frameTimer = CreateTimer(60)


;camera
Global cam = CreateCamera()
Global camX# = 64, camY# = 25, camZ# = -5
Global camPitch#, camYaw#, camRoll#
PositionEntity cam,camX,camY,camZ
CameraClsColor cam,100,180,255

;light
Global light = CreateLight()
RotateEntity light,45,0,0

;skybox
Global skyBox = LoadSkyBox("gfx/sky")
ScaleEntity skyBox,2,2,2
PositionEntity skyBox,camX,camY,camZ


;water
Global water = CreatePlane()
Global waterTexture = LoadTexture("gfx/water.png")
EntityTexture water, waterTexture
ScaleEntity water,5,1,5
EntityAlpha water,0.7

;very basic island
Global island = LoadTerrain("gfx/heightmap.png")
Global grassTexture = LoadTexture("gfx/grass.png")
ScaleEntity island,1,10,1
PositionEntity island,0,-4,0
EntityTexture island, grassTexture
TerrainShading island,True
TerrainDetail island,2000,1

;game pointer
HidePointer
Global gamePointer = LoadImage("gfx/pointer.png")
MaskImage gamePointer,255,255,255


;mouse
Global msx, msy, msz, msxSpeed, msySpeed, mszSpeed


GenerateTestObjects(100)


;MAIN LOOP
While(Not(KeyHit(1)))
  Cls()
  
  MoveCam()
  WaterMotion()
  
  UpdateWorld()
  RenderWorld()
  
  msx = MouseX()
  msy = MouseY()
  msz = MouseZ()
  msxSpeed = MouseXSpeed()
  msySpeed = MouseYSpeed()
  mszSpeed = MouseZSpeed()
  DrawImage gamePointer,msx,msy
  
  DebugInfo()
  
  Flip 0
Wend


Function LoadSkyBox( file$ )
	m=CreateMesh()
	;front face
	b=LoadBrush( file$+"-forward.png",49 )
	s=CreateSurface( m,b )
	AddVertex s,-1,+1,-1,0,0:AddVertex s,+1,+1,-1,1,0
	AddVertex s,+1,-1,-1,1,1:AddVertex s,-1,-1,-1,0,1
	AddTriangle s,0,1,2:AddTriangle s,0,2,3
	FreeBrush b
	;right face
	b=LoadBrush( file$+"-right.png",49 )
	s=CreateSurface( m,b )
	AddVertex s,+1,+1,-1,0,0:AddVertex s,+1,+1,+1,1,0
	AddVertex s,+1,-1,+1,1,1:AddVertex s,+1,-1,-1,0,1
	AddTriangle s,0,1,2:AddTriangle s,0,2,3
	FreeBrush b
	;back face
	b=LoadBrush( file$+"-back.png",49 )
	s=CreateSurface( m,b )
	AddVertex s,+1,+1,+1,0,0:AddVertex s,-1,+1,+1,1,0
	AddVertex s,-1,-1,+1,1,1:AddVertex s,+1,-1,+1,0,1
	AddTriangle s,0,1,2:AddTriangle s,0,2,3
	FreeBrush b
	;left face
	b=LoadBrush( file$+"-left.png",49 )
	s=CreateSurface( m,b )
	AddVertex s,-1,+1,+1,0,0:AddVertex s,-1,+1,-1,1,0
	AddVertex s,-1,-1,-1,1,1:AddVertex s,-1,-1,+1,0,1
	AddTriangle s,0,1,2:AddTriangle s,0,2,3
	FreeBrush b
	;top face
	b=LoadBrush( file$+"-top.png",49 )
	s=CreateSurface( m,b )
	AddVertex s,-1,+1,+1,0,1:AddVertex s,+1,+1,+1,0,0
	AddVertex s,+1,+1,-1,1,0:AddVertex s,-1,+1,-1,1,1
	AddTriangle s,0,1,2:AddTriangle s,0,2,3
	FreeBrush b
	;bottom face	
	b=LoadBrush( file$+"-bottom.png",49 )
	s=CreateSurface( m,b )
	AddVertex s,-1,-1,-1,1,0:AddVertex s,+1,-1,-1,1,1
	AddVertex s,+1,-1,+1,0,1:AddVertex s,-1,-1,+1,0,0
	AddTriangle s,0,1,2:AddTriangle s,0,2,3
	FreeBrush b
	ScaleMesh m,100,100,100
	FlipMesh m
	EntityFX m,1
	Return m
End Function

Function MoveCam()
  Local msSpeed# = 0.05
  Local msRotateSpeed# = 0.2
  Local msScrollSpeed# = 1.2
  
  camPitch = EntityPitch(cam)
  camYaw = EntityYaw(cam)
  camRoll = EntityRoll(cam)
	
	;move cam if player gets close to screen margins
  If(msx < 20 Or KeyDown(KEY_A)) Then camX = camX - msSpeed*Cos(camYaw) : camZ = camZ - msSpeed*Sin(camYaw)
  If(msx > screenWidth-20 Or KeyDown(KEY_D)) Then camX = camX + msSpeed*Cos(camYaw) : camZ = camZ + msSpeed*Sin(camYaw)
	If(msy < 20 Or KeyDown(KEY_W)) Then camX = camX - msSpeed*Sin(camYaw) : camZ = camZ + msSpeed*Cos(camYaw)
  If(msy > screenHeight-20 Or KeyDown(KEY_S)) Then camX = camX + msSpeed*Sin(camYaw) : camZ = camZ - msSpeed*Cos(camYaw)
  PositionEntity cam,camX,camY,camZ
  
  ;rotate cam if wheel key pressed
  If(MouseDown(MOUSE_WHEEL)) Then
    RotateEntity cam,camPitch+msySpeed*msRotateSpeed,camYaw-msxSpeed*msRotateSpeed,0
  EndIf
  
  ;zoom
  MoveEntity cam,0,0,mszSpeed*msScrollSpeed
  camX = EntityX(cam) : camY = EntityY(cam) : camZ = EntityZ(cam)
  
  ;move skybox with camera
  PositionEntity skyBox,camX,camY,camZ
  
End Function

Function DebugInfo()
  Color 0,255,0
  Text 0,0,"camX: " + camX
  Text 0,20,"camY: " + camY
  Text 0,40,"camZ: " + camZ
  Text 200,0,"camPitch: "+camPitch
  Text 200,20,"camYaw: "+camYaw
  Text 200,40,"camRoll: "+camRoll
End Function

Function GenerateTestObjects(number%)
  Local i, rndMesh, obj
  For i=1 To number
    rndMesh = Rnd(1,4)
    Select rndMesh
      Case 1 : obj = CreateCube()
      Case 2 : obj = CreateCone()
      Case 3 : obj = CreateSphere()
      Case 4 : obj = CreateCylinder()
    End Select
    PositionEntity obj,Rnd(1,128),0,Rnd(1,128)
    TranslateEntity obj,0,TerrainY(island,EntityX(obj),EntityY(obj),EntityZ(obj)),0
    EntityColor obj,Rnd(0,255),Rnd(0,255),Rnd(0,255)
  Next
End Function

Function WaterMotion()
  TurnEntity water,0,0.0005,0
End Function
;~IDEal Editor Parameters:
;~F#60
;~C#Blitz3D