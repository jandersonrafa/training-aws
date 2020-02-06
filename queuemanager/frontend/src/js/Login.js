import React, { Component } from 'react'
import { Row, Col, Card, CardTitle, TextInput, Collection, CollectionItem, Textarea } from 'react-materialize';
import HighlightedButton from './HighlightedButton';
import { updateAuth } from './actions';
import { bindActionCreators } from 'redux';
import { findAll, resubmit, remove } from './services/deadLetterService';
import { alertError, alertSuccess } from './services/alertService';
import Alert from 'react-s-alert';

import { connect } from 'react-redux';

class Login extends Component {
  constructor(props) {
    super(props);
    this.handleChange = this.handleChange.bind(this);
    this.removeMessage = this.removeMessage.bind(this)
    this.resubmitMessage = this.resubmitMessage.bind(this)
    this.clickResults = this.clickResults.bind(this);
    this.renderList = this.renderList.bind(this);

  }

  componentDidMount() {
    findAll().then(
      (result) => {
        this.setState({
          results: result
        })
        alertSuccess(result.length + ' encontradas!', "")
      },
      (error) => {
        alertError('Erro ao buscar mensagens', "")
      })
  }
  state = {
    results: [],
    messageSelected: {
      filteredOriginalHeaders: '',
      typeAction: '',
      originalMessage: ''
    },
    form: {
      id: '',
      resubmitHeaders: '',
      resubmitMessage: ''
    }
  }

  handleChange = evt => {
    this.setState({ form: { ...this.state.form, [evt.target.name]: event.target.value } });
  }

  clickResults(row, index) {
    this.state.results.forEach((r, i) => {
      r.active = i === index
    })

    this.setState({
      messageSelected: row,
    });

    this.setState({
      form: {
        ...this.state.form, id: row.id, resubmitHeaders: this.formatJson(row.resubmitHeaders)
        , resubmitMessage: this.formatJson(row.resubmitMessage),
        resubmitQueueName: row.resubmitQueueName
      }
    });
  }

  removeMessage() {
    let confirm = true
    if (this.state.messageSelected.typeAction === 'RESUBMITTED') {
      confirm = window.confirm("Mensagem já excluída, deseja continuar?");
    }

    if (confirm) {

      remove(this.state.form.id, this.state.form)
        .then(
          (result) => {
            alertSuccess('Mensagem excluída com sucesso!')
            this.setState({
              messageSelected: {
                ...this.state.messageSelected, typeAction: 'DELETED'
              }
            });
          },
          (error) => {
            alertError('Erro ao excluir', error.response.data.message)
          }
        )
      event.preventDefault()
    }
  }
  resubmitMessage() {
    let confirm = true
    if (this.state.messageSelected.typeAction === 'RESUBMITTED') {
      confirm = window.confirm("Mensagem já reenviada, deseja continuar?");
    }

    if (confirm) {
      resubmit(this.state.form.id, this.state.form)
        .then(
          (result) => {
            alertSuccess('Mensagem reenviada com sucesso!')
            this.setState({
              messageSelected: {
                ...this.state.messageSelected, typeAction: 'RESUBMITTED'
              }
            });
          },
          (error) => {
            alertError('Erro ao reenviar mensagem', '')
          }
        )
      event.preventDefault()
    }
  }

  renderList(row, index) {
    return (<CollectionItem className={row.active ? 'active' : ''} href="javascript:;" onClick={() => this.clickResults(row, index)}>
      {index} - {row.queueName} - {row.typeAction}
    </CollectionItem>)
  }

  formatJson(content) {
    return content ? JSON.stringify(JSON.parse(content), null, 2) : content
  }

  render() {
    return (
      <div class="login">
        <Row>
          <Col m={4} s={12}>
            <Card header={<CardTitle />}
              title="Mensagens encontrados" >
              <hr></hr>
              <Collection>
                {this.state.results.map(this.renderList)}
              </Collection>
            </Card>
          </Col>
          <Col m={7} s={12}>
            <form>
              <Card header={<CardTitle />}
                actions={[
                  <Col m={12} s={12}>
                    <Col m={6} s={6}><HighlightedButton onClick={this.removeMessage} text="Excluir"></HighlightedButton></Col>
                    <Col m={6} s={6}><HighlightedButton onClick={this.resubmitMessage} text="Reenviar"></HighlightedButton></Col>
                  </Col>,
                  <br></br>
                ]}
                title="Queue Dead Letter" >
                <div class="makerspace-detail__form-content" >
                  <Alert stack={{ limit: 2 }} />
                  <hr></hr>
                  <Row>
                    <Col m={12} s={12}>
                      <h6>Mensagem original</h6>
                      <TextInput m={9} s={9} disabled value={this.state.messageSelected.queueName} label="Original Queue" />
                      <TextInput m={3} s={3} disabled value={this.state.messageSelected.typeAction} label="Status" />
                      <Textarea m={12} s={12} disabled value={this.state.messageSelected.filteredOriginalHeaders} label="Headers" />
                      <Textarea m={12} s={12} disabled value={this.state.messageSelected.originalMessage} label="Message" />
                    </Col>
                    <Col m={12} s={12}>
                      <h6>Opções de reenvio</h6>
                      <Textarea m={12} s={12} name='resubmitHeaders' value={this.state.form.resubmitHeaders} onChange={this.handleChange} label="Change Headers" />
                      <Textarea m={12} s={12} name='resubmitMessage' value={this.state.form.resubmitMessage} onChange={this.handleChange} label="Change Message" />
                      <TextInput m={12} s={12} name='resubmitQueueName' value={this.state.form.resubmitQueueName} onChange={this.handleChange} label="Destination Queue" />
                    </Col>
                  </Row>
                </div>
              </Card>
            </form>
          </Col>
        </Row>
      </div>
    );
  }

}


const mapStateToProps = store => ({
  auth: store.authState.auth,
});
const mapDispatchToProps = dispatch =>
  bindActionCreators({ updateAuth }, dispatch);
export default connect(mapStateToProps, mapDispatchToProps)(Login);