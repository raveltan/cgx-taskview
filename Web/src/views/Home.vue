<template>
  <div class="home">
    <v-app-bar app class="blue white--text">
      <v-toolbar-title>CGX Taskview</v-toolbar-title>
    </v-app-bar>

    <v-content>
      <v-container class="fill-height" fluid>
        <v-row align="center" justify="center">
          <v-col cols="12" sm="8" md="4">
            <v-card class="elevation-12">
              <v-toolbar color="primary" dark flat>
                <v-toolbar-title>Find a Kanban 看板</v-toolbar-title>
              </v-toolbar>
              <v-card-text>
                <v-form @submit.prevent="">
                  <v-text-field
                    id="id"
                    label="Kanban ID"
                    name="kanbanId"
                    type="text"
                    v-model="uid"
                  ></v-text-field>
                </v-form>
              </v-card-text>
              <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="primary" block @click="find">Find</v-btn>
              </v-card-actions>
            </v-card>
          </v-col>
        </v-row>
      </v-container>
    </v-content>
  </div>
</template>

<script>
export default {
  data() {
    return {
      uid: '',
    }
  },
  methods: {
    find() {
      if (this.uid) {
        this.$router.push({ name: 'ViewData', params: { uid: this.uid } })
      }
    },
  },
}
</script>

<style></style>
