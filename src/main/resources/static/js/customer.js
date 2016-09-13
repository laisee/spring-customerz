angular.module('customers', ['ngResource', 'ui.bootstrap']).
    factory('Customers', function ($resource) {
        return $resource('customers');
    }).
    factory('Customer', function ($resource) {
        return $resource('customers/:id', {id: '@id'});
    }).
    factory("EditorStatus", function () {
        var editorEnabled = {};

        var enable = function (id, fieldName) {
            editorEnabled = { 'id': id, 'fieldName': fieldName };
        };

        var disable = function () {
            editorEnabled = {};
        };

        var isEnabled = function(id, fieldName) {
            return (editorEnabled['id'] == id && editorEnabled['fieldName'] == fieldName);
        };

        return {
            isEnabled: isEnabled,
            enable: enable,
            disable: disable
        }
    });

function CustomersController($scope, $modal, Customers, Customer, Status) {
    function list() {
        $scope.customers = Customers.query();
    }

    function clone (obj) {
        return JSON.parse(JSON.stringify(obj));
    }

    function saveCustomer(customer) {
        Customers.save(customer,
            function () {
                Status.success("Customer saved");
                list();
            },
            function (result) {
                Status.error("Error saving customer: " + result.status);
            }
        );
    }

    $scope.addCustomer = function () {
        var addModal = $modal.open({
            templateUrl: 'templates/customerForm.html',
            controller: CustomerModalController,
            resolve: {
                customer: function () {
                    return {};
                },
                action: function() {
                    return 'add';
                }
            }
        });

        addModal.result.then(function (customer) {
            saveCustomer(customer);
        });
    };

    $scope.updateCustomer = function (customer) {
        var updateModal = $modal.open({
            templateUrl: 'templates/customerForm.html',
            controller: CustomerModalController,
            resolve: {
                customer: function() {
                    return clone(customer);
                },
                action: function() {
                    return 'update';
                }
            }
        });

        updateModal.result.then(function (customer) {
            saveCustomer(customer);
        });
    };

    $scope.deleteCustomer = function (customer) {
        Customer.delete({id: customer.id},
            function () {
                Status.success("Customer deleted");
                list();
            },
            function (result) {
                Status.error("Error deleting customer: " + result.status);
            }
        );
    };

    $scope.setCustomersView = function (viewName) {
        $scope.customersView = "templates/" + viewName + ".html";
    };

    $scope.init = function() {
        list();
        $scope.setCustomersView("grid");
        $scope.sortField = "name";
        $scope.sortDescending = false;
    };
}

function CustomerModalController($scope, $modalInstance, customer, action) {
    $scope.customerAction = action;
    $scope.yearPattern = /^[1-2]\d{3}$/;
    $scope.customer = customer;

    $scope.ok = function () {
        $modalInstance.close($scope.customer);
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
};

function CustomerEditorController($scope, Customers, Status, EditorStatus) {
    $scope.enableEditor = function (customer, fieldName) {
        $scope.newFieldValue = customer[fieldName];
        EditorStatus.enable(customer.id, fieldName);
    };

    $scope.disableEditor = function () {
        EditorStatus.disable();
    };

    $scope.isEditorEnabled = function (customer, fieldName) {
        return EditorStatus.isEnabled(customer.id, fieldName);
    };

    $scope.save = function (customer, fieldName) {
        if ($scope.newFieldValue === "") {
            return false;
        }

        customer[fieldName] = $scope.newFieldValue;

        Customers.save({}, customer,
            function () {
                Status.success("Customer saved");
                list();
            },
            function (result) {
                Status.error("Error saving customer: " + result.status);
            }
        );

        $scope.disableEditor();
    };

    $scope.disableEditor();
}

angular.module('customers').
    directive('inPlaceEdit', function () {
        return {
            restrict: 'E',
            transclude: true,
            replace: true,

            scope: {
                ipeFieldName: '@fieldName',
                ipeInputType: '@inputType',
                ipeInputClass: '@inputClass',
                ipePattern: '@pattern',
                ipeModel: '=model'
            },

            template:
                '<div>' +
                    '<span ng-hide="isEditorEnabled(ipeModel, ipeFieldName)" ng-click="enableEditor(ipeModel, ipeFieldName)">' +
                        '<span ng-transclude></span>' +
                    '</span>' +
                    '<span ng-show="isEditorEnabled(ipeModel, ipeFieldName)">' +
                        '<div class="input-append">' +
                            '<input type="{{ipeInputType}}" name="{{ipeFieldName}}" class="{{ipeInputClass}}" ' +
                                'ng-required ng-pattern="{{ipePattern}}" ng-model="newFieldValue" ' +
                                'ui-keyup="{enter: \'save(ipeModel, ipeFieldName)\', esc: \'disableEditor()\'}"/>' +
                            '<div class="btn-group btn-group-xs" role="toolbar">' +
                                '<button ng-click="save(ipeModel, ipeFieldName)" type="button" class="btn"><span class="glyphicon glyphicon-ok"></span></button>' +
                                '<button ng-click="disableEditor()" type="button" class="btn"><span class="glyphicon glyphicon-remove"></span></button>' +
                            '</div>' +
                        '</div>' +
                    '</span>' +
                '</div>',

            controller: 'CustomerEditorController'
        };
    });
