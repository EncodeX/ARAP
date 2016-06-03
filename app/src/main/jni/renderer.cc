/**
* Copyright (c) 2015 VisionStar Information Technology (Shanghai) Co., Ltd. All Rights Reserved.
* EasyAR is the registered trademark or trademark of VisionStar Information Technology (Shanghai) Co., Ltd in China
* and other countries for the augmented reality technology developed by VisionStar Information Technology (Shanghai) Co., Ltd.
*/

#include "renderer.hpp"
#if defined __APPLE__
#include <OpenGLES/ES2/gl.h>
#else
#include <GLES2/gl2.h>
#include <android/log.h>

#endif

const char* box_vert=
        "uniform mat4 trans;\n"
        "uniform mat4 proj;\n"
        "attribute vec4 coord;\n"
        "attribute vec4 color;\n"
        "varying vec4 vcolor;\n"
        "\n"
        "void main(void)\n"
        "{\n"
        "    vcolor = color;\n"
        "    gl_Position = proj*trans*coord;\n"
        "}\n"
        "\n"
;

const char* box_frag=
        "#ifdef GL_ES\n"
        "precision highp float;\n"
        "#endif\n"
        "varying vec4 vcolor;\n"
        "\n"
        "void main(void)\n"
        "{\n"
        "    gl_FragColor = vcolor;\n"
        "}\n"
        "\n"
;

const char* box_video_vert="uniform mat4 trans;\n"
        "uniform mat4 proj;\n"
        "attribute vec4 coord;\n"
        "attribute vec2 texcoord;\n"
        "varying vec2 vtexcoord;\n"
        "\n"
        "void main(void)\n"
        "{\n"
        "    vtexcoord = texcoord;\n"
        "    gl_Position = proj*trans*coord;\n"
        "}\n"
        "\n"
;

const char* box_video_frag="#ifdef GL_ES\n"
        "precision highp float;\n"
        "#endif\n"
        "varying vec2 vtexcoord;\n"
        "uniform sampler2D texture;\n"
        "\n"
        "void main(void)\n"
        "{\n"
        "    gl_FragColor = texture2D(texture, vtexcoord);\n"
        "}\n"
        "\n"
;

namespace EasyAR{
namespace samples{

void Renderer::init()
{
    program_box = glCreateProgram();
    GLuint vertShader = glCreateShader(GL_VERTEX_SHADER);
    glShaderSource(vertShader, 1, &box_vert, 0);
    glCompileShader(vertShader);
    GLuint fragShader = glCreateShader(GL_FRAGMENT_SHADER);
    glShaderSource(fragShader, 1, &box_frag, 0);
    glCompileShader(fragShader);
    glAttachShader(program_box, vertShader);
    glAttachShader(program_box, fragShader);
    glLinkProgram(program_box);
    glUseProgram(program_box);
    pos_coord_box = glGetAttribLocation(program_box, "coord");
    pos_color_box = glGetAttribLocation(program_box, "color");
    pos_trans_box = glGetUniformLocation(program_box, "trans");
    pos_proj_box = glGetUniformLocation(program_box, "proj");

    glGenBuffers(1, &vbo_coord_box);
    glBindBuffer(GL_ARRAY_BUFFER, vbo_coord_box);
    const GLfloat cube_vertices[8][3] = {
        /* +z */{1.0f / 2, 1.0f / 2, 0.01f / 2}, {1.0f / 2, -1.0f / 2, 0.01f / 2}, {-1.0f / 2, -1.0f / 2, 0.01f / 2}, {-1.0f / 2, 1.0f / 2, 0.01f / 2},
        /* -z */{1.0f / 2, 1.0f / 2, -0.01f / 2}, {1.0f / 2, -1.0f / 2, -0.01f / 2}, {-1.0f / 2, -1.0f / 2, -0.01f / 2}, {-1.0f / 2, 1.0f / 2, -0.01f / 2}};
    glBufferData(GL_ARRAY_BUFFER, sizeof(cube_vertices), cube_vertices, GL_DYNAMIC_DRAW);

    glGenBuffers(1, &vbo_color_box);
    glBindBuffer(GL_ARRAY_BUFFER, vbo_color_box);
//    const GLubyte cube_vertex_colors[8][4] = {
//        {255, 0, 0, 128}, {0, 255, 0, 128}, {0, 0, 255, 128}, {0, 0, 0, 128},
//        {0, 255, 255, 128}, {255, 0, 255, 128}, {255, 255, 0, 128}, {255, 255, 255, 128}};
    const GLubyte cube_vertex_colors[8][4] = {
        {255, 255, 255, 255}, {255, 255, 255, 255}, {255, 255, 255, 255}, {255, 255, 255, 255},
        {255, 255, 255, 255}, {255, 255, 255, 255}, {255, 255, 255, 255}, {255, 255, 255, 255}};
    glBufferData(GL_ARRAY_BUFFER, sizeof(cube_vertex_colors), cube_vertex_colors, GL_STATIC_DRAW);

    glGenBuffers(1, &vbo_color_box_2);
    glBindBuffer(GL_ARRAY_BUFFER, vbo_color_box_2);
    const GLubyte cube_vertex_colors_2[8][4] = {
        {255, 0, 0, 255}, {255, 255, 0, 255}, {0, 255, 0, 255}, {255, 0, 255, 255},
        {255, 0, 255, 255}, {255, 255, 255, 255}, {0, 255, 255, 255}, {255, 0, 255, 255}};
    glBufferData(GL_ARRAY_BUFFER, sizeof(cube_vertex_colors_2), cube_vertex_colors_2, GL_STATIC_DRAW);

    glGenBuffers(1, &vbo_faces_box);
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo_faces_box);
    const GLushort cube_faces[6][4] = {
        /* +z */{3, 2, 1, 0}, /* -y */{2, 3, 7, 6}, /* +y */{0, 1, 5, 4},
        /* -x */{3, 0, 4, 7}, /* +x */{1, 2, 6, 5}, /* -z */{4, 5, 6, 7}};
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, sizeof(cube_faces), cube_faces, GL_STATIC_DRAW);
}

void Renderer::render(const Matrix44F& projectionMatrix, const Matrix44F& cameraview, Vec2F size)
{

    for(int i = 0; i<16; ++i){
        camera_data[i] = cameraview.data[i];
        projection_data[i] = projectionMatrix.data[i];
//        __android_log_print(ANDROID_LOG_INFO, "EasyAR", "%f", camera_data[i]);
    }

    glBindBuffer(GL_ARRAY_BUFFER, vbo_coord_box);
    float height = size[0] / 1000;
    const GLfloat cube_vertices[8][3] = {
        /* +z */{size[0] / 2, size[1] / 2, height / 2}, {size[0] / 2, -size[1] / 2, height / 2}, {-size[0] / 2, -size[1] / 2, height / 2}, {-size[0] / 2, size[1] / 2, height / 2},
        /* -z */{size[0] / 2, size[1] / 2, 0}, {size[0] / 2, -size[1] / 2, 0}, {-size[0] / 2, -size[1] / 2, 0}, {-size[0] / 2, size[1] / 2, 0}};
    glBufferData(GL_ARRAY_BUFFER, sizeof(cube_vertices), cube_vertices, GL_DYNAMIC_DRAW);
//    __android_log_print(ANDROID_LOG_INFO, "EasyAR", "%f %f", size[0], size[1]);

    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    glEnable(GL_DEPTH_TEST);
    glUseProgram(program_box);

    glBindBuffer(GL_ARRAY_BUFFER, vbo_coord_box);
    glEnableVertexAttribArray(pos_coord_box);
    glVertexAttribPointer(pos_coord_box, 3, GL_FLOAT, GL_FALSE, 0, 0);

    glBindBuffer(GL_ARRAY_BUFFER, vbo_color_box);
    glEnableVertexAttribArray(pos_color_box);
    glVertexAttribPointer(pos_color_box, 4, GL_UNSIGNED_BYTE, GL_TRUE, 0, 0);
    // 转换坐标与变换
    glUniformMatrix4fv(pos_trans_box, 1, 0, cameraview.data);
    glUniformMatrix4fv(pos_proj_box, 1, 0, projectionMatrix.data);

    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo_faces_box);
    for(int i = 0; i < 6; i++) {
        glDrawElements(GL_TRIANGLE_FAN, 4, GL_UNSIGNED_SHORT, (void*)(i * 4 * sizeof(GLushort)));
    }
//
//    glBindBuffer(GL_ARRAY_BUFFER, vbo_coord_box);
//    const GLfloat cube_vertices_2[8][3] = {
//        /* +z */{size[0] / 4, size[1] / 4, size[0] / 4},{size[0] / 4, -size[1] / 4, size[0] / 4},{-size[0] / 4, -size[1] / 4, size[0] / 4},{-size[0] / 4, size[1] / 4, size[0] / 4},
//        /* -z */{size[0] / 4, size[1] / 4, 0},{size[0] / 4, -size[1] / 4, 0},{-size[0] / 4, -size[1] / 4, 0},{-size[0] / 4, size[1] / 4, 0}};
//    glBufferData(GL_ARRAY_BUFFER, sizeof(cube_vertices_2), cube_vertices_2, GL_DYNAMIC_DRAW);
//    glEnableVertexAttribArray(pos_coord_box);
//    glVertexAttribPointer(pos_coord_box, 3, GL_FLOAT, GL_FALSE, 0, 0);
//
//    glBindBuffer(GL_ARRAY_BUFFER, vbo_color_box_2);
//    glEnableVertexAttribArray(pos_color_box);
//    glVertexAttribPointer(pos_color_box, 4, GL_UNSIGNED_BYTE, GL_TRUE, 0, 0);
//    for(int i = 0; i < 6; i++) {
//        glDrawElements(GL_TRIANGLE_FAN, 4, GL_UNSIGNED_SHORT, (void*)(i * 4 * sizeof(GLushort)));
//    }


}

    void VideoRenderer::init()
    {
        program_box = glCreateProgram();
        GLuint vertShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertShader, 1, &box_video_vert, 0);
        glCompileShader(vertShader);
        GLuint fragShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragShader, 1, &box_video_frag, 0);
        glCompileShader(fragShader);
        glAttachShader(program_box, vertShader);
        glAttachShader(program_box, fragShader);
        glLinkProgram(program_box);
        glUseProgram(program_box);
        pos_coord_box = glGetAttribLocation(program_box, "coord");
        pos_tex_box = glGetAttribLocation(program_box, "texcoord");
        pos_trans_box = glGetUniformLocation(program_box, "trans");
        pos_proj_box = glGetUniformLocation(program_box, "proj");

        glGenBuffers(1, &vbo_coord_box);
        glBindBuffer(GL_ARRAY_BUFFER, vbo_coord_box);
        const GLfloat cube_vertices[4][3] = {{1.0f / 2, 1.0f / 2, 0.f},{1.0f / 2, -1.0f / 2, 0.f},{-1.0f / 2, -1.0f / 2, 0.f},{-1.0f / 2, 1.0f / 2, 0.f}};
        glBufferData(GL_ARRAY_BUFFER, sizeof(cube_vertices), cube_vertices, GL_DYNAMIC_DRAW);

        glGenBuffers(1, &vbo_tex_box);
        glBindBuffer(GL_ARRAY_BUFFER, vbo_tex_box);
        const GLubyte cube_vertex_texs[4][2] = {{0, 0},{0, 1},{1, 1},{1, 0}};
        glBufferData(GL_ARRAY_BUFFER, sizeof(cube_vertex_texs), cube_vertex_texs, GL_STATIC_DRAW);

        glGenBuffers(1, &vbo_faces_box);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo_faces_box);
        const GLushort cube_faces[] = {3, 2, 1, 0};
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, sizeof(cube_faces), cube_faces, GL_STATIC_DRAW);

        glUniform1i(glGetUniformLocation(program_box, "texture"), 0);
        glGenTextures(1, &texture_id);
        glBindTexture(GL_TEXTURE_2D, texture_id);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    }

    void VideoRenderer::render(const Matrix44F& projectionMatrix, const Matrix44F& cameraview, Vec2F size)
    {
        glBindBuffer(GL_ARRAY_BUFFER, vbo_coord_box);
        const GLfloat cube_vertices[4][3] = {{size[0] / 2, size[1] / 2, 0.f},{size[0] / 2, -size[1] / 2, 0.f},{-size[0] / 2, -size[1] / 2, 0.f},{-size[0] / 2, size[1] / 2, 0.f}};
        glBufferData(GL_ARRAY_BUFFER, sizeof(cube_vertices), cube_vertices, GL_DYNAMIC_DRAW);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_DEPTH_TEST);
        glUseProgram(program_box);
        glBindBuffer(GL_ARRAY_BUFFER, vbo_coord_box);
        glEnableVertexAttribArray(pos_coord_box);
        glVertexAttribPointer(pos_coord_box, 3, GL_FLOAT, GL_FALSE, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, vbo_tex_box);
        glEnableVertexAttribArray(pos_tex_box);
        glVertexAttribPointer(pos_tex_box, 2, GL_UNSIGNED_BYTE, GL_FALSE, 0, 0);
        glUniformMatrix4fv(pos_trans_box, 1, 0, cameraview.data);
        glUniformMatrix4fv(pos_proj_box, 1, 0, projectionMatrix.data);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo_faces_box);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texture_id);
        glDrawElements(GL_TRIANGLE_FAN, 4, GL_UNSIGNED_SHORT, 0);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    unsigned int VideoRenderer::texId()
    {
        return texture_id;
    }

}
}
