module.exports = function (grunt) {

  // Project configuration.
  grunt.initConfig({
    coffeeify: {
      basic: {
        options: {},
        files: [{
          src: ['app/assets/scripts/admin.coffee', 'app/assets/scripts/controllers/admin/*.coffee'],
          dest: 'dist/admin.js'
        }, {
          src: ['app/assets/scripts/op.coffee', 'app/assets/scripts/controllers/op/*.coffee'],
          dest: 'dist/op.js'
        }, {
          src: ['app/assets/scripts/agent.coffee', 'app/assets/scripts/controllers/agent/*.coffee'],
          dest: 'dist/agent.js'
        }, {
          src: ['app/assets/scripts/directives/*.coffee'],
          dest: 'dist/all_directives.js'
        }, {
          src: ['app/assets/scripts/filters/*.coffee'],
          dest: 'dist/all_filters.js'
        }, {
          src: ['app/assets/scripts/services/*.coffee'],
          dest: 'dist/all_services.js'
        }]
      }
    },
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
    },
    clean: ['dist'],
    ngmin: {
      admin_controllers: {
        src: ['dist/admin.js'],
        dest: 'dist/ngmin/admin.js'
      },
      op_controllers: {
        src: ['dist/op.js'],
        dest: 'dist/ngmin/op.js'
      },
      agent_controllers: {
        src: ['dist/agent.js'],
        dest: 'dist/ngmin/agent.js'
      },
      directives: {
        src: ['dist/all_directives.js'],
        dest: 'dist/ngmin/all_directives.js'
      },
      filters: {
        src: ['dist/all_filters.js'],
        dest: 'dist/ngmin/all_filters.js'
      },
      services: {
        src: ['dist/all_services.js'],
        dest: 'dist/ngmin/all_services.js'
      }
    },
    concat: {
      //admin: {
      //  src: ['app/assets/scripts/admin.js', 'app/assets/scripts/controllers/admin/*.js'],
      //  dest: 'dist/admin.js'
      //},
      //op: {
      //  src: ['app/assets/scripts/op.js', 'app/assets/scripts/controllers/op/*.js'],
      //  dest: 'dist/op.js'
      //},
      //agent: {
      //  src: ['app/assets/scripts/agent.js', 'app/assets/scripts/controllers/agent/*.js'],
      //  dest: 'dist/agent.js'
      //},
      //directives: {
      //  src: ['app/assets/scripts/directives/*.js'],
      //  dest: 'dist/all_directives.js'
      //},
      //filters: {
      //  src: ['app/assets/scripts/filters/*.js'],
      //  dest: 'dist/all_filters.js'
      //},
      //services: {
      //  src: ['app/assets/scripts/services/*.js'],
      //  dest: 'dist/all_services.js'
      //}
      d3_services: {
        src: ['dist/all_services.js', 'app/assets/scripts/services/d3.js'],
        dest: 'dist/all_services.js'
      }
    },
    uglify: {
      options: {
        mangle: false
      },
      my_target: {
        files: {
          'app/assets/scripts/min/op.min.js': ['dist/ngmin/op.js'],
          'app/assets/scripts/min/agent.min.js': ['dist/ngmin/agent.js'],
          'app/assets/scripts/min/admin.min.js': ['dist/ngmin/admin.js'],
          'app/assets/scripts/min/directives.min.js': ['dist/ngmin/all_directives.js'],
          'app/assets/scripts/min/filters.min.js': ['dist/ngmin/all_filters.js'],
          'app/assets/scripts/min/services.min.js': ['dist/ngmin/all_services.js']
        }
      }
    }
  });

  grunt.loadNpmTasks('grunt-coffeeify');
  grunt.loadNpmTasks('grunt-bower-task');
  grunt.loadNpmTasks('grunt-contrib-copy');
  grunt.loadNpmTasks('grunt-contrib-concat');
  grunt.loadNpmTasks('grunt-ngmin');
  grunt.loadNpmTasks('grunt-contrib-uglify');
  grunt.loadNpmTasks('grunt-contrib-clean');

  // Default task(s).
  grunt.registerTask('default', ['bower:install', 'copy']);
  grunt.registerTask('minjs', ['clean', 'coffeeify', 'concat', 'ngmin', 'uglify']);

};