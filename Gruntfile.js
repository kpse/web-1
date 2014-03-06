module.exports = function (grunt) {

  // Project configuration.
  grunt.initConfig({
    bower: {
      install: {
        options: {
          targetDir: './public/vendor',
          layout: 'byType',
          install: true,
          verbose: true,
          cleanTargetDir: true,
          cleanBowerDir: false,
          bowerOptions: {}
        }
      }
    },
    copy: {
      main: {
        expand: true,
        flatten: true,
        src: 'public/vendor/fonts/bootstrap/*',
        dest: 'public/vendor/css/fonts'
      }
    }
  });

  grunt.loadNpmTasks('grunt-bower-task');
  grunt.loadNpmTasks('grunt-contrib-copy');

  // Default task(s).
  grunt.registerTask('default', ['bower:install','copy']);

};