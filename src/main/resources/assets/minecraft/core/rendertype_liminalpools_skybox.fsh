#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;
uniform sampler2D Sampler2;
uniform sampler2D Sampler3;
uniform sampler2D Sampler4;
uniform sampler2D Sampler5;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

uniform mat4 ProjMat;
uniform mat4 ModelViewMat;

uniform mat4 RotMat;

in float vertexDistance;
in vec4 vertexColor;
in vec2 texCoord0;
in vec4 normal;

in vec4 texProj0;
in vec4 glPos;

out vec4 fragColor;

vec2 sampleCube(vec3 v, out int faceIndex) {
    vec3 vAbs = abs(v);
    float ma;
    vec2 uv;
    if (vAbs.z >= vAbs.x && vAbs.z >= vAbs.y) {
        faceIndex = v.z < 0 ? 1 : 3;
        ma = 0.5 / vAbs.z;
        uv = vec2(v.z < 0.0 ? -v.x : v.x, -v.y);
    } else if (vAbs.y >= vAbs.x) {
        faceIndex = v.y < 0 ? 5 : 0;
        ma = 0.5 / vAbs.y;
        uv = vec2(-v.x, v.y < 0.0 ? v.z : -v.z);
    } else {
        faceIndex = v.x < 0 ? 4 : 2;
        ma = 0.5 / vAbs.x;
        uv = vec2(v.x < 0.0 ? v.z : -v.z, -v.y);
    }
    return uv * ma + 0.5;
}

void main() {
    float near = 0.05;
    float far = (ProjMat[2][2]-1.)/(ProjMat[2][2]+1.) * near;
    int faceIndex = 0;
    vec4 texPos = vec4(sampleCube(normalize((inverse(ProjMat * RotMat) * vec4(glPos.xy / glPos.w * (far - near), far + near, far - near)).xyz), faceIndex), 1.0, 1.0);
    texPos = vec4(-texPos.x, texPos.y, texPos.z, texPos.w);

    vec4 color = textureProj(Sampler0, texPos);

    if (faceIndex == 0) {
        color = textureProj(Sampler0, texPos);
    } else if (faceIndex == 1) {
        color = textureProj(Sampler1, texPos);
    } else if (faceIndex == 2) {
        color = textureProj(Sampler2, texPos);
    } else if (faceIndex == 3) {
        color = textureProj(Sampler3, texPos);
    } else if (faceIndex == 4) {
        color = textureProj(Sampler4, texPos);
    } else if (faceIndex == 5) {
        color = textureProj(Sampler5, texPos);
    }

    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}
