import axios from 'axios'

export function PlayerService(apiRoot) {
    this.createPlayer = (playerDetails) => axios
        .post(apiRoot + '/api/players', { name: playerDetails.name })
        .then(response => response.data)

    this.getPlayer = (playerId) => axios
        .get(apiRoot + '/api/players/' + playerId)
        .then(response => response.data)
}

export function TaskService(apiRoot) {
    this.getTasks = (playerId) => axios
        .get(apiRoot + `/api/players/${playerId}/tasks`)
        .then(response => response.data)

    this.createTask = (playerId, title, deadline) => axios
        .post(apiRoot + `/api/players/${playerId}/tasks`, { title, deadline: deadline || undefined })
        .then(response => response.data)

    this.createRecurringTask = (playerId, title, cadence, duration) => axios
        .post(apiRoot + `/api/players/${playerId}/recurring-tasks`, { title, cadence, duration })
        .then(response => response.data)

    this.completeTask = (taskId) => axios
        .put(apiRoot + `/api/tasks/${taskId}/complete`)
        .then(response => response.data)
}
