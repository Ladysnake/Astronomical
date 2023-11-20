#version 150

#moj_import <projection.glsl>
#moj_import <light.glsl>
#moj_import <fog.glsl>

in vec3 Position;
in vec4 Color;
in vec2 UV0;
in ivec2 UV1;
in ivec2 UV2;
in vec3 Normal;

uniform sampler2D Sampler2;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform vec3 ChunkOffset;
uniform int FogShape;

out float vertexDistance;
out vec4 vertexColor;
out vec2 texCoord0;
out vec4 normal;

out vec4 lightMapColor;
out vec4 texProj0;
out vec4 glPos;

void main() {
    vec3 pos = Position + ChunkOffset;
    gl_Position = ProjMat * ModelViewMat * vec4(pos, 1.0);
    vertexDistance = fog_distance(ModelViewMat, pos, FogShape);
    vertexColor = Color * minecraft_sample_lightmap(Sampler2, UV2);

    glPos = gl_Position;
    texProj0 = projection_from_position(gl_Position);

    texCoord0 = UV0;
    normal = ProjMat * ModelViewMat * vec4(Normal, 0.0);
}
