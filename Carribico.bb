AppTitle "Carribico"

Global screenWidth = 1024
Global screenHeight = 768
Global colorDepth = 32
Global screenMode = 2

;Constants
Const KEY_LEFT = 203
Const KEY_RIGHT = 205
Const KEY_UP = 200
Const KEY_DOWN = 208

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
RotateEntity light,0,0,45

;water
Global water = CreatePlane()
Global waterTexture = LoadTexture("gfx/water.png")
EntityTexture water, waterTexture

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



;MAIN LOOP
While(Not(KeyHit(1)))
  Cls()
  
  MoveCam()
  
  UpdateWorld()
  RenderWorld()
  
  msx = MouseX()
  msy = MouseY()
  msz = MouseZ()
  msxSpeed = MouseXSpeed()
  msySpeed = MouseYSpeed()
  mszSpeed = MouseZSpeed()
  DrawImage gamePointer,msx,msy
  
  Flip 0
Wend


Function MoveCam()
  Local msSpeed# = 0.05
  Local msRotateSpeed# = 0.2
  Local msScrollSpeed# = -1.2
  
  camPitch = EntityPitch(cam)
  camYaw = EntityYaw(cam)
  camRoll = EntityRoll(cam)
	
	;move cam if player gets close to screen margins
  If(msx < 20 Or KeyDown(KEY_LEFT)) Then TranslateEntity cam,-msSpeed*Cos(camYaw),0,-msSpeed*Sin(camYaw)
  If(msx > screenWidth-20 Or KeyDown(KEY_RIGHT)) Then TranslateEntity cam,msSpeed*Cos(camYaw),0,msSpeed*Sin(camYaw)
	If(msy < 20 Or KeyDown(KEY_UP)) Then TranslateEntity cam,-msSpeed*Sin(camYaw),0,msSpeed*Cos(camYaw)
  If(msy > screenHeight-20 Or KeyDown(KEY_DOWN)) Then TranslateEntity cam,msSpeed*Sin(camYaw),0,-msSpeed*Cos(camYaw)
  
  ;rotate cam if wheel key pressed
  If(MouseDown(MOUSE_WHEEL)) Then
    RotateEntity cam,camPitch+msySpeed*msRotateSpeed,camYaw-msxSpeed*msRotateSpeed,0
  EndIf
  
  ;zoom
  TranslateEntity cam,0,mszSpeed*msScrollSpeed,0
  
End Function
;~IDEal Editor Parameters:
;~C#Blitz3D