import React, { Component } from 'react'
import { Select, Row, Col, Checkbox, Card, CardTitle, TextInput, Collection, CollectionItem, Textarea } from 'react-materialize';
import HighlightedButton from './HighlightedButton';
import { updateAuth } from './actions';
import { bindActionCreators } from 'redux';
import { findAll, filter, resubmit, resubmitMessages, remove } from './services/deadLetterService';
import { alertError, alertSuccess } from './services/alertService';
import Alert from 'react-s-alert';
import { connect } from 'react-redux';

class Login extends Component {
  constructor(props) {
    super(props);
    this.handleChange = this.handleChange.bind(this);
    this.handleChangeCheckbox = this.handleChangeCheckbox.bind(this);
    this.selectFilterTypeAction = this.selectFilterTypeAction.bind(this);
    this.removeMessage = this.removeMessage.bind(this)
    this.resubmitMessage = this.resubmitMessage.bind(this)
    this.resubmitCheckedMessages = this.resubmitCheckedMessages.bind(this)
    this.clickResults = this.clickResults.bind(this);
    this.renderList = this.renderList.bind(this);
    this.filterMessages = this.filterMessages.bind(this);
  }

  filterMessages() {
    filter(this.state.filter).then(
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

  selectFilterTypeAction(event) {
    this.setState({ filter: { ...this.state.filter, typeAction: event.target.value } });

  }

  componentDidMount() {
    this.filterMessages()
  }
  state = {
    results: [],
    checkeds: [],
    filter: {
      typeAction: 'PENDENT'
    },
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

  handleChangeCheckbox = (isChecked, id) => {
    if (isChecked) {
      this.state.checkeds.push(id)
    } else {
      this.state.checkeds.pop(id)
    }
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

  resubmitCheckedMessages() {
    if (!this.state.checkeds || this.state.checkeds.length === 0) {
      alertError('Nenhuma mensagem selecionada')
    }
    const confirm = window.confirm("Tem certeza que deseja reenviar as " + this.state.checkeds.length + " mensagens");
    if (confirm) {
      resubmitMessages(this.state.checkeds)
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
            const erro = error && error.response && error.response.data && error.response.data.message && error.response.data.message
            alertError('Erro ao reenviar mensagem', erro)
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
            const erro = error && error.response && error.response.data && error.response.data.message && error.response.data.message
            alertError('Erro ao reenviar mensagem', erro)
          }
        )
      event.preventDefault()
    }
  }

  renderList(row, index) {
    return (<CollectionItem className={row.active ? 'active' : ''} href="javascript:;" onClick={() => this.clickResults(row, index)}>
      <Checkbox
        id={row.id}
        label='- '
        value={row.id}
        onChange={(evt) => this.handleChangeCheckbox(evt.target.checked, row.id)}
      />
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
              <Row>
                <br></br>
                <Col m={12} className={'messages_filter'}>
                <Col m={4}>
                  <Select 
                    onChange={this.selectFilterTypeAction}
                    options={{
                      classes: '',
                      dropdownOptions: {
                        alignment: 'left',
                        autoTrigger: true,
                        closeOnClick: true,
                        constrainWidth: true,
                        container: null,
                        coverTrigger: true,
                        hover: false,
                        inDuration: 150,
                        onCloseEnd: null,
                        onCloseStart: null,
                        onOpenEnd: null,
                        onOpenStart: null,
                        outDuration: 250
                      }
                    }}
                    value={this.state.filter.typeAction}
                  >
                    <option
                      disabled
                      value=""
                    >
                      Choose your option
  </option>
                    <option value="PENDENT">
                      PENDENT
  </option>
                    <option value="DELETED">
                      DELETED
  </option>
                    <option value="RESUBMITTED">
                      RESUBMITTED
  </option>
                  </Select>
                  </Col>
                  <Col m={4}>
                    <HighlightedButton onClick={this.filterMessages} text="Pesquisar"></HighlightedButton>
                  </Col>
                  <Col m={4}>
                    <HighlightedButton onClick={this.resubmitCheckedMessages} text="Reenviar"></HighlightedButton>
                  </Col>
                </Col>
              </Row>
              <hr></hr>
              <Collection m={12} s={12}>
                {this.state.results.map(this.renderList)}
              </Collection>
            </Card>
          </Col>
          {this.state.messageSelected.id ?
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
                        <TextInput m={4} s={4} disabled value={this.state.messageSelected.id} label="Message ID" />
                        <TextInput m={6} s={6} disabled value={this.state.messageSelected.queueName} label="Original Queue" />
                        <TextInput m={2} s={2} disabled value={this.state.messageSelected.typeAction} label="Status" />
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
            : null}
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