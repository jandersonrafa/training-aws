import api from './api';

export const resubmit= (id, body) => {
  return api.put('/dead-letter/resubmit/' + id, body)
    .then(res => res.data)
}

export const resubmitMessages= (list) => {
  return api.put('/dead-letter/resubmit-messages', list)
    .then(res => res.data)
}

export const remove= (id, body) => {
  return api.put('/dead-letter/delete/' + id, body)
    .then(res => res.data)
}

export const findAll= (params) => {
  return api.get('/dead-letter/')
    .then(res => res.data)
}

export const filter= (filter) => {
  return api.get('/dead-letter/filter', addParams(filter, null))
    .then(res => res.data)
}

const addParams = (params, config) => {
	return Object.assign({}, { params }, config)
}