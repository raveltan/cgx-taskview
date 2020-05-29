<template>
  <div class="view">
    <v-overlay :value="loading">
      <v-progress-circular indeterminate :size="64"></v-progress-circular
    ></v-overlay>
    <v-navigation-drawer app v-model="nav" v-if="!error">
      <v-list v-if="!loading && data">
        <v-subheader>TASKS</v-subheader>
        <v-list-item-group color="primary">
          <v-list-item
            v-for="(item, i) in data.projects"
            :key="i"
            :color="'blue'"
            @click="current = item"
          >
            <v-list-item-content>
              <v-list-item-title v-text="item"></v-list-item-title>
            </v-list-item-content>
          </v-list-item>
        </v-list-item-group>
      </v-list>
    </v-navigation-drawer>
    <v-app-bar app class="blue" dark>
      <v-app-bar-nav-icon @click="nav = !nav"></v-app-bar-nav-icon>
      <v-toolbar-title
        >CGX Taskview {{ current ? ' - ' : '' }} {{ current }}</v-toolbar-title
      >
    </v-app-bar>
    <v-content>
      <v-container fluid>
        <h1 v-if="current == '' && !error">Select a task</h1>
        <h1 v-if="error == true">Project not found</h1>
        <div class="data" v-if="current != '' && data">
          <v-row>
            <v-col cols="12" md="4">
              <v-card dark class="yellow darken-4">
                <v-card-title class="headline">Planned</v-card-title>
                <v-list-item
                  v-for="(item, i) in data[current]
                    ? data[current].planned
                    : []"
                  :key="i"
                  class="white"
                  light
                >
                  <v-list-item-content>
                    <v-list-item-title v-text="item"></v-list-item-title>
                  </v-list-item-content>
                </v-list-item>
              </v-card>
            </v-col>
            <v-col cols="12" md="4">
              <v-card dark class="green darken-2">
                <v-card-title class="headline">Progress</v-card-title>
                <v-list-item
                  v-for="(item, i) in data[current]
                    ? data[current].progress
                    : []"
                  :key="i"
                  class="white"
                  light
                >
                  <v-list-item-content>
                    <v-list-item-title v-text="item"></v-list-item-title>
                  </v-list-item-content>
                </v-list-item>
              </v-card>
            </v-col>
            <v-col cols="12" md="4">
              <v-card class="blue darken-2" dark>
                <v-card-title class="headline">Done</v-card-title>
                <v-list-item
                  v-for="(item, i) in data[current] ? data[current].done : []"
                  :key="i"
                  class="white"
                  light
                >
                  <v-list-item-content>
                    <v-list-item-title v-text="item"></v-list-item-title>
                  </v-list-item-content>
                </v-list-item>
              </v-card>
            </v-col>
          </v-row>
        </div>
      </v-container>
    </v-content>
  </div>
</template>

<script>
import db from '@/firebase.js'
export default {
  data() {
    return {
      error: false,
      current: '',
      nav: true,
      data: null,
      loading: true,
    }
  },
  props: ['uid'],
  created() {
    db.ref(this.uid).on('value', (sn) => {
      this.loading = false
      this.data = sn.val()
      if (!sn.val()) this.error = true
    })
  },
}
</script>

<style></style>
